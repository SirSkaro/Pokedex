package skaro.pokeflex.api;

import reactor.core.publisher.Mono;

public class RequestURL implements PokeFlexRequest
{
	private String url;
	private Endpoint endpoint;
	
	public RequestURL(String url, Endpoint endpoint)
	{
		this.url = url;
		this.endpoint = endpoint;
	}
	
	@Override
	public Endpoint getEndpoint() { return endpoint; }
	public String getURL() { return url; }
	
	@Override
	public Mono<IFlexObject> makeRequest(PokeFlexFactory factory)
	{
		return factory.createFlexObject(this);
	}
}
