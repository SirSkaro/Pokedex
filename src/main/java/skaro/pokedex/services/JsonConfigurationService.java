package skaro.pokedex.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;				//in Discord4J's dependencies
import com.fasterxml.jackson.databind.ObjectMapper;			//in Discord4J's dependencies

import skaro.pokedex.core.ResourceManager;

public class JsonConfigurationService implements PokedexService
{
	private static String dataKey = "production".intern();
	private static JsonConfigurationService instance;
	private static JsonNode rootNode;
	
	private JsonConfigurationService(ConfigurationType type)
	{
		dataKey = type.getKey();
		
		try 
        {
			ObjectMapper objectMapper = new ObjectMapper();
	        InputStream config = ResourceManager.getConfigurationResource("config.json");
			rootNode = objectMapper.readTree(config);
		} 
        catch (IOException e) 
        {
			System.out.println("[Congifurator] Unable to parse configuration file (config.json). Exiting..");
			System.exit(1);
		}
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.CONFIG;
	}
	
	public static JsonConfigurationService initialize(ConfigurationType type)
	{
		if(instance != null)
			return instance;
			
		instance = new JsonConfigurationService(type);
		return instance;
	}
	
	public static Optional<JsonConfigurationService> getInstance()
	{
		return Optional.ofNullable(instance);
	}
	
	public String getVersion()
	{
		return rootNode.get("version".intern()).asText();
	}
	
	public Optional<String>getAuthToken(String application)
	{
		return getConfigData("token".intern(), application);
	}
	
	public Optional<String> getPatreonAuthToken() {
		return getConfigData("access_token", "patreon");
	}
	
	private Optional<String> getConfigData(String data, String application)
	{
		JsonNode dataNode = rootNode.get(application);
		if(dataNode == null)
			return Optional.empty();	//No configuration data for application
		
		String result = dataNode.get(data).get(dataKey).asText();
		
		if(result.equals(""))
			return Optional.empty();
		return Optional.of(result);
	}
	
	public Optional<String> getPublishAuthToken(String recipient)
	{
		JsonNode dataNode = rootNode.get("publish_recipients");
		if(dataNode == null)
			return Optional.empty();	//No configuration data for application
		
		String result = dataNode.get(recipient).get("token").get(dataKey).asText();
		
		if(result.equals(""))
			return Optional.empty();
		return Optional.of(result);
	}
	
	public int getPublishDesignatedShard(String recipient)
	{
		JsonNode dataNode = rootNode.get("publish_recipients");
		
		return dataNode.get(recipient).get("designated_shard").asInt();
	}
	
	public String[] getDBCredentials()
	{
		JsonNode dataNode = rootNode.get("sql");
		if(dataNode == null)
			throw new IllegalStateException("No database congifuration data could be found.");
		
		String[] credentials = new String[4];
		credentials[0] = dataNode.get("username").asText();
		credentials[1] = dataNode.get("database").asText();
		credentials[2] = dataNode.get("password").asText();
		credentials[3] = dataNode.get("uri").asText();
		
		return credentials;
	}
	
	public String getModelBasePath()
	{
		JsonNode dataNode = rootNode.get("model_path");
		if(dataNode == null)
			throw new IllegalStateException("No database congifuration data could be found.");
		
		return dataNode.get(dataKey).asText();
	}
	
	public String getHelpGifPath()
	{
		JsonNode dataNode = rootNode.get("help_gif_path");
		if(dataNode == null)
			throw new IllegalStateException("No database congifuration data could be found.");
		
		return dataNode.get(dataKey).asText();
	}
	
	public String getZMoveClipPath()
	{
		JsonNode dataNode = rootNode.get("zmove_clip_path");
		if(dataNode == null)
			throw new IllegalStateException("No database congifuration data could be found.");
		
		return dataNode.get(dataKey).asText();
	}
	
	public String getPokeFlexURL()
	{
		JsonNode dataNode = rootNode.get("pokeflex_url");
		if(dataNode == null)
			throw new IllegalStateException("No database congifuration data could be found.");
		
		return dataNode.get(dataKey).asText();
	}
	
}
