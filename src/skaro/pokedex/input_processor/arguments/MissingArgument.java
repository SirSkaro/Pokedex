package skaro.pokedex.input_processor.arguments;

import java.util.List;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class MissingArgument extends InvalidCommandArgument
{
	private List<Class<? extends CommandArgument>> argumentCategory;
	
	public MissingArgument(List<Class<? extends CommandArgument>> arguments)
	{
		super(arguments);
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
	
	public List<Class<? extends CommandArgument>> getArgumentCategory()
	{
		return argumentCategory;
	}

	@Override
	public String createErrorMessage()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(":small_red_triangle_down:").append("You must also specify a ");
		
		for(Class<? extends CommandArgument> argumentCategory : argumentCategories)
			builder.append("`").append(argumentCategory.getSimpleName()).append("`").append(" or ");
		
		return builder.substring(0, builder.lastIndexOf(" or "));
	}
}
