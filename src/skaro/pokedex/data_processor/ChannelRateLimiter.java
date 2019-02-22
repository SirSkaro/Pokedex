package skaro.pokedex.data_processor;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import discord4j.core.object.util.Snowflake;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

public class ChannelRateLimiter
{
	private Cache<Long, Bucket> bucketCache;
	private int messagesPerPeriod;
	private Duration bandwidthPeriod;
	
	public ChannelRateLimiter(int allowedMessagesPerPeriod, Duration periodOfBandwidth)
	{
		messagesPerPeriod = allowedMessagesPerPeriod;
		bandwidthPeriod = periodOfBandwidth;
		
		bucketCache = Caffeine.newBuilder()
				.weakValues()
				.expireAfterAccess(bandwidthPeriod)
				.maximumSize(50)
				.build();
	}
	
    public boolean channelIsRateLimited(Snowflake channelSnowflake)
    {
    	Long channelID = channelSnowflake.asLong();
    	Bucket bucketForChannel = bucketCache.getIfPresent(channelID);
    	
    	if(bucketForChannel == null)
    		bucketForChannel = registerNewBucket(channelID);
    	
    	return !bucketForChannel.tryConsume(1);
    }
    
    private Bucket registerNewBucket(Long channelID)
    {
    	Bucket newBucket = createBucketForChannel(channelID);
		bucketCache.put(channelID, newBucket);
		
		return newBucket;
    }
    
    private Bucket createBucketForChannel(Long channelID)
    {
    	Bandwidth limit = Bandwidth.simple(messagesPerPeriod, bandwidthPeriod);
    	Bucket bucket = Bucket4j.builder()
    						.addLimit(limit)
    						.build();
    	
    	return bucket;
    }
    
}
