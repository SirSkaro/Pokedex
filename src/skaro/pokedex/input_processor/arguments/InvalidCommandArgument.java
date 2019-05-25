package skaro.pokedex.input_processor.arguments;

import java.util.List;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public abstract class InvalidCommandArgument extends CommandArgument
{
	protected List<Class<? extends CommandArgument>> argumentCategories;
	
	public InvalidCommandArgument(List<Class<? extends CommandArgument>> arguments)
	{
		super("Invalid", null);
		argumentCategories = arguments;
		this.valid = false;
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
	
	public abstract String createErrorMessage();
}
