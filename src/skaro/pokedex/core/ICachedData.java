package skaro.pokedex.core;

import skaro.pokeflex.api.IFlexObject;

public interface ICachedData
{
	public IFlexObject getByName(String name);
}
