package skaro.pokedex.services;

import discord4j.core.DiscordClient;

public class DiscordService implements IService 
{
	private DiscordClient v3Client;
	
	public DiscordService(DiscordClient v3Client)
	{
		this.v3Client = v3Client;
	}

	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.DISCORD;
	}
	
	public DiscordClient getV3Client() { return this.v3Client; }
}
