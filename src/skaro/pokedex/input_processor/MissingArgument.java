package skaro.pokedex.input_processor;

public class MissingArgument extends CommandArgument
{
	public MissingArgument()
	{
		this.valid = false;
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
}
