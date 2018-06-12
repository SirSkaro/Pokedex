package skaro.pokedex.input_processor;

public enum InputErrorStatus 
{
	NO_ERROR("Input is valid"),
	ARGUMENT_NUMBER("Mismatch in expected number of arguments"),
	INVALID_ARGUMENT("Some argument is invalid");
	
	private String description;
	
	private InputErrorStatus(String desc)
	{
		description = desc;
	}
	
	public String getDesc() { return description; }
}
