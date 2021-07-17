package skaro.pokedex.services;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;
import skaro.pokedex.input_processor.MySQLManager;

public class PerkService implements PokedexService, PokedexServiceConsumer
{
	private Cache<Long, Pledge> pledgeCache;
	private PatreonAPI patreonClient;
	private PokedexServiceManager services;
	private PerkTierManager tierManager;
	private MySQLManager sqlManager = MySQLManager.getInstance();
	private static final Snowflake SUPPORT_SERVER_ID = Snowflake.of(339583821072564255L);
	
	public PerkService(PatreonAPI patreonClient, PerkTierManager tierManager)
	{
		this.tierManager = tierManager;
		this.patreonClient = patreonClient;
		
		pledgeCache = Caffeine.newBuilder()
				.weakValues()
				.expireAfterAccess(Duration.ofHours(30L))
				.maximumSize(50)
				.build();
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) {
		return services.hasServices(ServiceType.DISCORD);
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.PERK;
	}
	
	public void setServiceManager(PokedexServiceManager services) throws ServiceConsumerException
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
	
	public Mono<Boolean> ownerOfGuildHasPerksForTier(Guild guildToCheck, PerkTier tier)
	{
		if(guildToCheck == null)
			return Mono.just(false);
		return Mono.just(guildToCheck)
				.filter(guild -> !guild.getId().equals(SUPPORT_SERVER_ID))
				.flatMap(guild -> guild.getOwner())
				.flatMap(owner -> userHasPerksForTier(owner, tier))
				.switchIfEmpty(Mono.just(false));
	}
	
	public Mono<User> getPokemonsAdopterIfPledged(String pokemon) {
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
		Pledge usersPledge = pledgeCache.getIfPresent(userDiscordId);
		if(usersPledge != null)
			return Mono.just(usersPledge);
		
		return getAndCachePledgeForUser(userDiscordId);
	}
	
	private Mono<Pledge> getAndCachePledgeForUser(long userDiscordId)
	{
		for(Pledge pledge : getAllPledges())
		{
			com.patreon.resources.User userPatronInfo = pledge.getPatron();
			Optional<String> userDiscordID = extractDiscordId(userPatronInfo);
			if(userDiscordID.isPresent() && Long.parseLong(userDiscordID.get()) == userDiscordId)
			{
				pledgeCache.put(userDiscordId, pledge);
				return Mono.just(pledge);
			}
		}
		
		return Mono.empty();
	}
	
	private List<Pledge> getAllPledges() {
		try {
			JSONAPIDocument<List<Campaign>> apiResponse = patreonClient.fetchCampaigns();
			Campaign campagin = apiResponse.get().get(0);
			return patreonClient.fetchAllPledges(campagin.getId());
		} catch(Exception e) {
			System.out.println("[PerkService] Could not fetch Patreon data.");
			e.printStackTrace();
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
			System.out.println("[PerkService] Unable to get Discord data for patron "+ user.getFullName());
			return Optional.empty();
		}
	}

}
