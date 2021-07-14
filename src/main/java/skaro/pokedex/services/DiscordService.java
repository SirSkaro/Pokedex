package skaro.pokedex.services;

import discord4j.core.GatewayDiscordClient;

public class DiscordService implements PokedexService 
{
	private GatewayDiscordClient v3Client;
	
	public DiscordService(GatewayDiscordClient v3Client)
	{
		this.v3Client = v3Client;
	}

	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.DISCORD;
	}
	
	public GatewayDiscordClient getV3Client() { return this.v3Client; }
}
