package skaro.pokedex.communicator;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.common.util.Snowflake;
import skaro.pokedex.communicator.publish_recipients.PublicationRecipient;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.DiscordService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceManager;
import skaro.pokedex.services.ServiceType;

public class Publisher implements PokedexServiceConsumer {
	private ServiceManager services;
	private List<PublicationRecipient> recipients;
	private ScheduledExecutorService executor;
	
	public Publisher(ServiceManager services, List<PublicationRecipient> recipients, ScheduledExecutorService executor) throws ServiceConsumerException {
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
		this.executor = executor;
		this.recipients = recipients;
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) {
		return services.hasServices(ServiceType.DISCORD, ServiceType.CONFIG);
	}
	
	public void schedulePublicationFrequency(int period, TimeUnit timeUnit) {
		int[] shards = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getShardIndexes();
		executor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() { 
				Snowflake botId = getBotId();
				long numberOfGuildsToReport = getNumberOfConnectedGuilds() / shards.length;
				for(PublicationRecipient recipient : recipients) {
					for(int i = 0; i < shards.length; i++) {
						try {
							recipient.sendPublication((int)numberOfGuildsToReport, botId.asLong(), shards[i]);
						} catch(Exception e) { 
							System.out.println("[Publisher] failed to send publication for "+recipient.getClass().getSimpleName());
						}
					}
				}
		}}
		, period, period, timeUnit);
	}
	
	private long getNumberOfConnectedGuilds() {
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getGuilds()
				.count()
				.block();
	}
	
	private Snowflake getBotId() {
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getSelfId();
	}
	
}
