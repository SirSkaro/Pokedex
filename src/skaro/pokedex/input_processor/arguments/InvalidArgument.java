package skaro.pokedex.input_processor.arguments;

import java.util.List;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class InvalidArgument extends InvalidCommandArgument
{
	public InvalidArgument(List<Class<? extends CommandArgument>> arguments)
	{
		super(arguments);
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
	
	public String createErrorMessage()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\"").append(this.rawInput).append("\"");
		builder.append(" is not a recognized ");
		
		for(Class<? extends CommandArgument> argumentCategory : argumentCategories)
			builder.append(argumentCategory.getName()).append(" or ");
		
		return builder.substring(0, builder.lastIndexOf(" or "));
	}
}
