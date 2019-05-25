package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class VersionArgument extends CommandArgument
{
	public VersionArgument()
	{
		super("Version", SQLResource.VERSION);
	}
}
