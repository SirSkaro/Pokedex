package skaro.pokedex.communicator;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.common.util.Snowflake;
import skaro.pokedex.communicator.publish_recipients.PublicationRecipient;
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
		return services.hasServices(ServiceType.DISCORD);
	}
	
	public void schedulePublicationFrequency(int period, TimeUnit timeUnit) {
		executor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() { 
				Snowflake botId = getBotId();
				
				for(PublicationRecipient recipient : recipients) {
					try {
						recipient.sendPublication(getNumberOfConnectedGuilds(), botId.asLong());
					} catch(Exception e) { 
						System.out.println("[Publisher] failed to send publication for "+recipient.getClass().getSimpleName());
					}
				}
		}}
		, period, period, timeUnit);
	}
	
	private int getNumberOfConnectedGuilds() {
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getGuilds()
				.collectList()
				.block()
				.size();
	}
	
	private Snowflake getBotId() {
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getSelfId();
	}
	
}
