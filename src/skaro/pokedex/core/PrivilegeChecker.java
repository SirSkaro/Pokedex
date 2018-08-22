package skaro.pokedex.core;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import com.patreon.resources.User;

import skaro.pokedex.input_processor.MySQLManager;
import sx.blah.discord.handle.obj.IUser;

public class PrivilegeChecker 
{
	private Cache<Long, IUser> privilegedUserCache;
	private PatreonAPI patreonClient;
	private MySQLManager sqlManager;
	
	public PrivilegeChecker(PatreonAPI client)
	{
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("privilegedUserCache",
		        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, IUser.class,
		            ResourcePoolsBuilder.heap(500))
		            .withExpiry(Expirations.timeToLiveExpiration(new Duration(30, TimeUnit.MINUTES)))
		            .build()).build(true);
		
		privilegedUserCache = cacheManager.getCache("privilegedUserCache", Long.class, IUser.class);
		
		patreonClient = client;
		sqlManager = MySQLManager.getInstance();
	}
	
	public boolean userIsPrivileged(IUser user)
	{
		if(privilegedUserCache.containsKey(user.getLongID()) || sqlManager.userIsDiscordVIP(user.getLongID()))
			return true;
		
		Optional<User> patronCheck = getPatronByDiscordID(user.getLongID());
		if(patronCheck.isPresent())
		{
			privilegedUserCache.put(user.getLongID(), user);
			return true;
		}
		
		return false;
	}
	
	private Optional<User> getPatronByDiscordID(Long id)
	{
		JSONAPIDocument<List<Campaign>> apiResponse;
		Campaign campagin;
		User user;
		String userDiscordID;
		List<Pledge> pledges;
		
		try 
		{
			apiResponse = patreonClient.fetchCampaigns();
			campagin = apiResponse.get().get(0);
			pledges = patreonClient.fetchAllPledges(campagin.getId());
			
			for(Pledge pledge : pledges)
			{
				user = pledge.getPatron();
				userDiscordID = user.getSocialConnections().getDiscord().getUser_id();
				if(userDiscordID != null && Long.parseLong(userDiscordID) == id)
					return Optional.of(user);
			}
		} 
		catch(Exception e) 
		{
			System.out.println("[PrivilegeChecker] Could not fetch Patreon data.");
			e.printStackTrace();
			return Optional.empty();
		}
		
		return Optional.empty();
	}
}
