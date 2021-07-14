package skaro.pokeflex.api;

import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Mono;

public class RequestQuery implements PokeFlexRequest 
{
	private Endpoint endpoint;
	private Map<String, String> params;
	
	public RequestQuery(Endpoint endpoint, String key, String value) {
		this.endpoint = endpoint;
		params = new HashMap<>();
		params.put(key, value);
	}
	
	public RequestQuery(Endpoint endpoint, Map<String, String> params) {
		this.endpoint = endpoint;
		this.params = params;
	}
	
	@Override
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public Mono<IFlexObject> makeRequest(PokeFlexFactory factory) {
		return factory.createFlexObject(endpoint, params);
	}

}
