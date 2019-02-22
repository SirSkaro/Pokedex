package skaro.pokedex.services;

import java.util.List;
import java.util.Optional;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;
import skaro.pokedex.input_processor.MySQLManager;

public class PerkService implements IService, IServiceConsumer
{
	private PatreonAPI patreonClient;
	private IServiceManager services;
	private MySQLManager sqlManager;
	
	public PerkService(PatreonAPI pClient)
	{
		patreonClient = pClient;
		sqlManager = MySQLManager.getInstance();
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return services.hasServices(ServiceType.DISCORD);
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.PERK;
	}
	
	public void setServiceManager(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	public boolean userHasCommandPrivileges(User user)
	{
		Long id = user.getId().asLong();
		if(sqlManager.userIsDiscordVIP(id))
			return true;
		
		Optional<com.patreon.resources.User> patronCheck = getPatronByDiscordID(id);
		if(patronCheck.isPresent())
			return true;
		
		return false;
	}
	
	public Mono<User> getPokemonsAdopter(String pokemon)
	{
		long userID ;
		Optional<Long> adoptionCheck = sqlManager.getPokemonsAdopter(pokemon);
		Mono<User> result;
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		
		//Check if user has adopted Pokemon. If not, return
		if(!adoptionCheck.isPresent())
			return Mono.empty();
		
		userID = adoptionCheck.get();
		result = discordService.getV3Client().getUserById(Snowflake.of(userID));
		
		//Ask Patreon's API if user is still pledged
		if(!sqlManager.userIsDiscordVIP(userID))
			result = result.filter(user -> getPatronByDiscordID(userID).isPresent());
		
		return result;
	}
	
	private Optional<com.patreon.resources.User> getPatronByDiscordID(Long id)
	{
		JSONAPIDocument<List<Campaign>> apiResponse;
		Campaign campagin;
		com.patreon.resources.User user;
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
	
	private Optional<String> getDiscordID(com.patreon.resources.User user)
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
