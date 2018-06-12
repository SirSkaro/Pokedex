package skaro.pokedex.core;

import java.io.InputStream;

public class ResourceManager 
{	
	public static InputStream getDictionaryResource(String fileName)
	{
		return ResourceManager.class.getResourceAsStream("/dictionaries/"+fileName);
	}
	
	public static InputStream getConfigurationResource(String fileName)
	{
		return ResourceManager.class.getResourceAsStream("/configurations/"+fileName);
	}
}
