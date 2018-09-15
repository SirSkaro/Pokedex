package skaro.pokedex.core;

import java.io.InputStream;

import skaro.pokedex.input_processor.Language;

public class ResourceManager 
{	
	public static InputStream getDictionaryResource(String fileName, Language lang)
	{
		return ResourceManager.class.getResourceAsStream("/dictionaries/"+ lang.getFlexKey() + "/" + fileName);
	}
	
	public static InputStream getConfigurationResource(String fileName)
	{
		return ResourceManager.class.getResourceAsStream("/configurations/"+fileName);
	}
}
