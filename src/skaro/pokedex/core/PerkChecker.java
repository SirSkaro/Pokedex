package skaro.pokedex.core;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import com.patreon.resources.User;

import skaro.pokedex.input_processor.MySQLManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

public class PerkChecker implements IService
{
	private Cache<Long, IUser> privilegedUserCache;
	private PatreonAPI patreonClient;
	private IDiscordClient discordClient;
	private MySQLManager sqlManager;
	
	public PerkChecker(PatreonAPI pClient, ScheduledExecutorService ses)
	{
		privilegedUserCache = Caffeine.newBuilder()
				.maximumSize(5)
				.executor(ses)
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.build();
		
		patreonClient = pClient;
		sqlManager = MySQLManager.getInstance();
	}
	
	public void setDiscordClient(IDiscordClient client)
	{
		discordClient = client;
	}
	
	public IUser fetchDiscordUser(long userID)
	{
		if(privilegedUserCache.asMap().containsKey(userID))
			return privilegedUserCache.getIfPresent(userID);
		
		return discordClient.fetchUser(userID);
	}
	
	public boolean userHasCommandPrivileges(IUser user)
	{
		if(privilegedUserCache.asMap().containsKey(user.getLongID()) || sqlManager.userIsDiscordVIP(user.getLongID()))
			return true;
		
		Optional<User> patronCheck = getPatronByDiscordID(user.getLongID());
		if(patronCheck.isPresent())
		{
			privilegedUserCache.put(user.getLongID(), user);
			return true;
		}
		
		return false;
	}
	
	public Optional<IUser> getPokemonsAdopter(String pokemon)
	{
		long userID ;
		Optional<Long> adoptionCheck = sqlManager.getPokemonsAdopter(pokemon);
		Optional<IUser> result;
		
		//Check if user has adopted Pokemon. If not, return
		if(!adoptionCheck.isPresent())
			return Optional.empty();
		userID = adoptionCheck.get();
		result = Optional.ofNullable(discordClient.fetchUser(userID));
		
		//Insure that Discoord fetched the adopter
		if(!result.isPresent())
			return Optional.empty();
		
		//Check if user is still a Patron. First check the cache
		if(privilegedUserCache.asMap().containsKey(userID) || sqlManager.userIsDiscordVIP(userID))
			return result;
			
		//Ask Patreon's API if user is still pledged. If so, cache the user and return the adopted Pokemon
		Optional<User> patronCheck = getPatronByDiscordID(userID);
		if(patronCheck.isPresent())
		{
			privilegedUserCache.put(userID, result.get());
			return result;
		}
	
		//Otherwise, the adopter has unpledged :[
		return Optional.empty();
	}
	
	private Optional<User> getPatronByDiscordID(Long id)
	{
		JSONAPIDocument<List<Campaign>> apiResponse;
		Campaign campagin;
		User user;
		Optional<String> userDiscordID;
		List<Pledge> pledges;
		
		try 
		{
			apiResponse = patreonClient.fetchCampaigns();
			campagin = apiResponse.get().get(0);
			pledges = patreonClient.fetchAllPledges(campagin.getId());
			
			for(Pledge pledge : pledges)
			{
				user = pledge.getPatron();
				userDiscordID = getDiscordID(user);
				if(userDiscordID.isPresent() && Long.parseLong(userDiscordID.get()) == id)
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
	
	private Optional<String> getDiscordID(User user)
	{
		try
		{
			if(user.getSocialConnections() == null
					|| user.getSocialConnections().getDiscord() == null)
				return Optional.empty();
			
			String userDiscordID = user.getSocialConnections().getDiscord().getUser_id();
			if(userDiscordID != null)
				return Optional.of(userDiscordID);
			
			return Optional.empty();
		}
		catch(Exception e)
		{
			System.out.println("[PrivilegeChecker] Unable to get Discord data for patron "+ user.getFullName());
			return Optional.empty();
		}
	}
}
