package skaro.pokedex.input_processor;

public class InvalidArgument extends CommandArgument
{
	public InvalidArgument()
	{
		this.valid = false;
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
}
