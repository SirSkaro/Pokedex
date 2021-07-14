package skaro.pokedex.services;

public enum ConfigurationType 
{
	PRODUCTION("production"),
	DEVELOP("debug"),
	;
	
	private String key;
	
	private ConfigurationType(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
}
