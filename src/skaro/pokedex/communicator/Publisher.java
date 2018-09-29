package skaro.pokedex.communicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ehcache.Cache;
import org.ehcache.Cache.Entry;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import skaro.pokedex.communicator.publish_recipients.BotsDiscordRecipient;
import skaro.pokedex.communicator.publish_recipients.CarbonitexRecipient;
import skaro.pokedex.communicator.publish_recipients.DiscordBotsRecipient;
import sx.blah.discord.api.IDiscordClient;

public class Publisher 
{
	private Cache<String, AbstractPublicationRecipient> publicationRecipientCache;
	private int shardID, totalShards;
	private ScheduledExecutorService executor;
	
	public Publisher(int shard, int shardCount, ScheduledExecutorService ses)
	{
		shardID = shard;
		totalShards = shardCount;
		executor = ses;
		
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
			.withCache("publicationRecipientCache",
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AbstractPublicationRecipient.class,
							ResourcePoolsBuilder.heap(50))
					.build())
			.build(true);
		
		publicationRecipientCache = cacheManager.getCache("publicationRecipientCache", String.class, AbstractPublicationRecipient.class);
	}
	
	public void populatePublicationRecipients(IDiscordClient discordClient)
	{
		List<AbstractPublicationRecipient> recipients = getRecipientList(discordClient);

		for(AbstractPublicationRecipient recipient : recipients)
		{
			if(recipient.configure() && recipient.isDesignatedShard(shardID))
			{
				System.out.println("[Publisher] added recipient "+recipient.getConfigID());
				publicationRecipientCache.put(recipient.getConfigID(), recipient);
			}
		}
	}
	
	public void scheduleHoursPerPublishment(int frequency)
	{
		executor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() { 
				for(Iterator<Entry<String, AbstractPublicationRecipient>> itr = publicationRecipientCache.iterator(); itr.hasNext(); )
				{
					AbstractPublicationRecipient recipient = itr.next().getValue();
					try {recipient.sendPublication(shardID);}
					catch(Exception e) { System.out.println("[Publisher] failed to send publication for "+recipient.getConfigID());};
				}
		}}
		, 1, 1, TimeUnit.HOURS);
	}
	
	private List<AbstractPublicationRecipient> getRecipientList(IDiscordClient discordClient)
	{
		List<AbstractPublicationRecipient> result = new ArrayList<AbstractPublicationRecipient>();

		result.add(new CarbonitexRecipient(discordClient, totalShards));
		result.add(new DiscordBotsRecipient(discordClient, totalShards));
		result.add(new BotsDiscordRecipient(discordClient, totalShards));
		return result;
	}
}
