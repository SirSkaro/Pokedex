package skaro.pokedex.communicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import discord4j.core.object.util.Snowflake;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.DiscordService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceManager;
import skaro.pokedex.services.ServiceType;

public class Publisher implements IServiceConsumer
{
	private ServiceManager services;
	private Cache<String, PublicationRecipient> publicationRecipientCache;
	private int shardId, totalShards;
	private ScheduledExecutorService executor;
	
	public Publisher(PublisherBuilder builder) throws ServiceConsumerException
	{
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = builder.services;
		this.shardId = builder.shardId;
		this.totalShards = builder.totalShards;
		this.executor = builder.executor;
		
		publicationRecipientCache = Caffeine.newBuilder()
				.maximumSize(builder.recipients.size())
				.executor(executor)
				.build();
		
		ConfigurationService configService = (ConfigurationService)services.getService(ServiceType.CONFIG);
		
		builder.recipients.stream()
			.filter(recipient -> recipient.configure(configService))
			.filter(recipient -> recipient.isDesignatedShard(shardId))
			.forEach(recipient -> publicationRecipientCache.put(recipient.getConfigID(), recipient));
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services)
	{
		return services.hasServices(ServiceType.DISCORD, ServiceType.CONFIG);
	}

	public static PublisherBuilder newBuilder()
	{
		return new PublisherBuilder();
	}
	
	public void scheduleHoursPerPublishment(int frequency)
	{
		executor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() { 
				Optional<Snowflake> botId = getBotId();
				
				if(!botId.isPresent())
					return;
				
				for(PublicationRecipient recipient : publicationRecipientCache.asMap().values())
				{
					try {recipient.sendPublication(shardId, totalShards, getNumberOfConnectedGuilds(), botId.get().asLong());}
					catch(Exception e) { System.out.println("[Publisher] failed to send publication for "+recipient.getConfigID());};
				}
		}}
		, frequency, frequency, TimeUnit.HOURS);
	}
	
	private int getNumberOfConnectedGuilds()
	{
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getGuilds()
				.collectList()
				.block()
				.size();
	}
	
	private Optional<Snowflake> getBotId()
	{
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getSelfId();
	}
	
	public static class PublisherBuilder
	{
		private int shardId, totalShards;
		private ScheduledExecutorService executor;
		private List<PublicationRecipient> recipients;
		private ServiceManager services;
		
		public PublisherBuilder()
		{
			shardId = -1;
			totalShards = -1;
			recipients = new ArrayList<>();
		}
		
		public PublisherBuilder setShard(int id)
		{
			this.shardId = id;
			return this;
		}
		
		public PublisherBuilder setTotalShards(int total)
		{
			this.totalShards = total;
			return this;
		}
		
		public PublisherBuilder setExecutor(ScheduledExecutorService executor)
		{
			this.executor = executor;
			return this;
		}
		
		public PublisherBuilder addRecipient(PublicationRecipient recipient)
		{
			recipients.add(recipient);
			return this;
		}
		
		public PublisherBuilder addServices(ServiceManager services)
		{
			this.services = services;
			return this;
		}
		
		public Publisher build() throws ServiceConsumerException 
		{
			return new Publisher(this);
		}
	}
	
}
