package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class PokemonArgument extends CommandArgument
{
	public PokemonArgument()
	{
		super();
		this.category = ArgumentCategory.POKEMON;
		this.sqlResource = SQLResource.POKEMON;
	}
}
