package skaro.pokedex.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;
import skaro.pokedex.input_processor.MySQLManager;

public class PerkService implements IService, IServiceConsumer
{
	private PatreonAPI patreonClient;
	private IServiceManager services;
	private PerkTierManager tierManager;
	private MySQLManager sqlManager;
	
	public PerkService(PatreonAPI pClient, PerkTierManager tierManager)
	{
		this.tierManager = tierManager;
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
	
	public Mono<Boolean> userHasPerksForTier(User user, PerkTier tier)
	{
		Long id = user.getId().asLong();
		if(sqlManager.userIsDiscordVIP(id))
			return Mono.just(true);
		
		return getPledgeForUser(user)
				.map(pledge -> tierManager.isInTierOrHigher(pledge.getAmountCents(), tier))
				.switchIfEmpty(Mono.just(false));
	}
	
	public Mono<Boolean> ownerOfGuildHasPerksForTier(Guild guild, PerkTier tier)
	{
		return guild.getOwner()
				.flatMap(owner -> userHasPerksForTier(owner, tier));
	}
	
	public Mono<User> getPokemonsAdopterIfPledged(String pokemon)
	{
		Optional<Long> adoptionCheck = sqlManager.getPokemonsAdopter(pokemon);
		if(!adoptionCheck.isPresent())
			return Mono.empty();
		
		return getDiscordUserFromId(adoptionCheck.get())
				.filterWhen(user -> userHasPerksForTier(user, PerkTier.VETERAN));
	}
	
	private Mono<User> getDiscordUserFromId(long id)
	{
		DiscordService discordService = (DiscordService)services.getService(ServiceType.DISCORD);
		return discordService.getV3Client().getUserById(Snowflake.of(id));
	}
	
	private Mono<Pledge> getPledgeForUser(User user)
	{
		long userDiscordId = user.getId().asLong();
		return getPledgeForUser(userDiscordId);
	}
	
	private Mono<Pledge> getPledgeForUser(long userDiscordId)
	{
		List<Pledge> pledges = getAllPledges();
		
		for(Pledge pledge : pledges)
		{
			com.patreon.resources.User userPatronInfo = pledge.getPatron();
			Optional<String> userDiscordID = extractDiscordId(userPatronInfo);
			if(userDiscordID.isPresent() && Long.parseLong(userDiscordID.get()) == userDiscordId)
				return Mono.just(pledge);
		}
		
		return Mono.empty();
	}
	
	private List<Pledge> getAllPledges()
	{
		try
		{
			JSONAPIDocument<List<Campaign>> apiResponse = patreonClient.fetchCampaigns();
			Campaign campagin = apiResponse.get().get(0);
			return campagin.getPledges();
		}
		catch(Exception e)
		{
			System.out.println("[PrivilegeChecker] Could not fetch Patreon data.");
			return Collections.emptyList();
		}
	}
	
	private Optional<String> extractDiscordId(com.patreon.resources.User user)
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
