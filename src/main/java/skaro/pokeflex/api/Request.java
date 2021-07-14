package skaro.pokeflex.api;

import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Mono;

public class Request implements PokeFlexRequest
{
	private Endpoint endpoint;
	private List<String> urlParams;
	
	public Request(Endpoint ep, List<String> params)
	{
		endpoint = ep;
		urlParams = params;
	}
	
	public Request(Endpoint ep, String param)
	{
		endpoint = ep;
		urlParams = new ArrayList<String>();
		urlParams.add(param);
	}
	
	public Request(Endpoint ep)
	{
		endpoint = ep;
		urlParams = new ArrayList<String>();
	}
	
	public void addParam(String s)
	{
		urlParams.add(s);
	}

	@Override
	public Endpoint getEndpoint() { return endpoint; }
	public List<String> getUrlParams() { return urlParams; }

	@Override
	public Mono<IFlexObject> makeRequest(PokeFlexFactory factory)
	{
		return factory.createFlexObject(this);
	}
}
