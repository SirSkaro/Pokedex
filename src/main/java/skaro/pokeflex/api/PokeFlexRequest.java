package skaro.pokeflex.api;

import reactor.core.publisher.Mono;

public interface PokeFlexRequest
{
	public Endpoint getEndpoint();
	public Mono<IFlexObject> makeRequest(PokeFlexFactory factory);
}
