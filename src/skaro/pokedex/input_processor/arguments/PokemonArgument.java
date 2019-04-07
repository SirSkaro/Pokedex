package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class PokemonArgument extends CommandArgument
{
	public PokemonArgument()
	{
		super("Pokemon", SQLResource.POKEMON);
	}
}
