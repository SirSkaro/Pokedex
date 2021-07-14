package skaro.pokeflex.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public class PokeFlexFactory 
{
	private String baseURI;
	private ObjectMapper mapper;

	public PokeFlexFactory(String base)
	{
		baseURI = base;
		mapper = new ObjectMapper();
	}

	public Mono<IFlexObject> createFlexObject(Endpoint endpoint, List<String> params) 
	{
		return Mono.justOrEmpty(constructURL(endpoint.getEnpoint(), params))
			.map(url -> makeRequest(endpoint, url));
	}
	
	public Mono<IFlexObject> createFlexObject(String url, Endpoint endpoint) 
	{
		List<String> params = getURLParams(url, endpoint);
		return createFlexObject(endpoint, params);
	}
	
	public Mono<IFlexObject> createFlexObject(Endpoint endpoint, Map<String, String> params) {
		return Mono.just(constructUrlWithQuery(endpoint.getEnpoint(), params))
				.map(url -> makeRequest(endpoint, url));
	}
	
	public Mono<IFlexObject> createFlexObject(Request request) 
	{
		return createFlexObject(request.getEndpoint(), request.getUrlParams());
	}
	
	public Mono<IFlexObject> createFlexObject(RequestURL request) 
	{
		return createFlexObject(request.getURL(), request.getEndpoint());
	}

	public Flux<IFlexObject> createFlexObjects(List<PokeFlexRequest> requests) 
	{
		return Flux.fromIterable(requests)
				.flatMap(request -> request.makeRequest(PokeFlexFactory.this));
	}
	
	public Flux<IFlexObject> createFlexObjects(List<PokeFlexRequest> requests, Scheduler scheduler) 
	{
		return Flux.fromIterable(requests)
				.parallel()
				.runOn(scheduler)
				.flatMap(request -> request.makeRequest(PokeFlexFactory.this))
				.sequential();
	}
	
	private IFlexObject makeRequest(Endpoint endpoint, URL url) 
	{
		Class<?> wrapperClass = endpoint.getWrapperClass();
		
		try 
		{
			String json = getJSONFromURL(url);
			return (IFlexObject)mapper.readValue(json, wrapperClass); 
		} 
		catch(IOException e) {
			throw Exceptions.propagate(e);
		}
	}
	
	private Optional<URL> constructURL(String endpoint, List<String> args)
	{
		Optional<URL> result;
		URL url;
		StringBuilder builder = new StringBuilder(baseURI);
		String builtURL;

		//Construct URL
		builder.append("/");
		builder.append(endpoint);
		builder.append("/");
		for(String arg: args)
		{
			builder.append(arg);
			builder.append("/");
		}

		//Build the URL
		builtURL = builder.substring(0, builder.lastIndexOf("/"));
		try 
		{
			url = new URL(builtURL);
			result = Optional.of(url);
		} 
		catch(MalformedURLException e) 
		{
			System.err.println(e.getMessage());
			result = Optional.empty();
		}

		return result;
	}
	
	private URL constructUrlWithQuery(String endpoint, Map<String, String> params) 
	{
		String queries = params.entrySet().stream()
			.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
			.collect(Collectors.joining("&"));

		StringBuilder builder = new StringBuilder(baseURI);
		builder.append("/").append(endpoint);
		builder.append("?").append(queries);
		
		try 
		{
			return new URL(builder.toString());
		} 
		catch (MalformedURLException e) 
		{
			throw Exceptions.propagate(e);
		}
	}

	private String getJSONFromURL(URL url) throws IOException
	{
		String htmlContent, jsonText;

		htmlContent = readContentFromUrl(url);
		jsonText = filterHTML(htmlContent);

		return jsonText;
	}

	private String filterHTML(String htmlContent)
	{
		String filteredContent = Jsoup.parse(htmlContent).text();
		int jsonStartBracketIndex = filteredContent.indexOf("{");
		int jsonEndBracketIndex = filteredContent.lastIndexOf("}");

		return filteredContent.substring(jsonStartBracketIndex, jsonEndBracketIndex + 1); 
	}

	private String readContentFromUrl(URL url) throws IOException 
	{
		InputStream is = url.openStream();
		try 
		{
			BufferedReader rd = new BufferedReader
					(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readBufferToString(rd);
			return jsonText;
		} 
		finally 
		{
			is.close();
		}
	}

	private String readBufferToString(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while((cp = rd.read()) != -1) 
		{
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	private List<String> getURLParams(String url, Endpoint endpoint)
	{
		List<String> result = new ArrayList<String>();
		String[] elements = url.split("/");
		int itr;
		
		for(itr = 0; itr < elements.length; itr++)
		{
			if(elements[itr].equals(endpoint.getEnpoint()))
				break;
		}
		
		if(elements.length <= (itr+1))
			return result;			//no url parameters
		
		for(itr = itr + 1; itr < elements.length; itr++)
			result.add(elements[itr]);
		
		return result;
	}
}
