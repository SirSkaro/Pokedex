package skaro.pokedex.data_processor.commands;

import java.io.File;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.ConfigurationService;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;

public class ShinyCommand extends AbstractCommand 
{
	private final String baseModelPath;
	private final String defaultPokemon;
	
	public ShinyCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "shiny".intern();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		baseModelPath = ConfigurationService.getInstance().get().getModelBasePath();
		defaultPokemon = "jirachi";
		
		aliases.put("schillerndes", Language.GERMAN);
		aliases.put("fāguāng", Language.CHINESE_SIMPMLIFIED);
		aliases.put("faguang", Language.CHINESE_SIMPMLIFIED);
		aliases.put("chromatique", Language.FRENCH);
		aliases.put("cromatico", Language.ITALIAN);
		aliases.put("irochi", Language.JAPANESE_HIR_KAT);
		aliases.put("irochigai", Language.JAPANESE_HIR_KAT);
		aliases.put("bichnaneun", Language.KOREAN);
		aliases.put("variocolor", Language.SPANISH);
		aliases.put("vario", Language.SPANISH);
		
		aliases.put("빛나는", Language.KOREAN);
		aliases.put("色違い", Language.JAPANESE_HIR_KAT);
		aliases.put("发光", Language.CHINESE_SIMPMLIFIED);

		createHelpMessage("Ponyta", "Solgaleo", "Keldeo resolute", "eevee",
				"https://i.imgur.com/FLBOsD5.gif");
	}

	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.COLOR);
	}

	@Override
	public Response discordReply(Input input, IUser requester) 
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);

		PerkChecker perkService = (PerkChecker)services.getService(ServiceType.PERK);
		
		if(!perkService.userHasCommandPrivileges(requester))
			return createNonPrivilegedReply(input);
		
		try
		{
			PokeFlexFactory factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			MultiMap<Object> dataMap = new MultiMap<Object>();
			EmbedCreateSpec builder = new EmbedCreateSpec();
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			Pokemon pokemon = Pokemon.class.cast(flexObj);

			flexObj = factory.createFlexObject(new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()));
			PokemonSpecies species = PokemonSpecies.class.cast(flexObj);

			//Add data to datamap
			dataMap.put(Pokemon.class.getName(), pokemon);
			dataMap.put(PokemonSpecies.class.getName(), species);
			
			//Add adopter
			addAdopter(pokemon, builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1012b", e); 
			e.printStackTrace();
			return response;
		}
	}

	private Response createNonPrivilegedReply(Input input)
	{
		String path;
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Response response = new Response();
		EmbedCreateSpec builder = new EmbedCreateSpec();

		try
		{
			//format embed
			//Easter egg: if the user specifies the default non-privilaged Pokemon, use the Patreon logo instead
			if(!input.getArg(0).getDbForm().equals(defaultPokemon))
			{
				builder.setImage("attachment://jirachi.gif");
				builder.setColor(colorService.getColorForType("psychic"));
				path = baseModelPath + "/"+ defaultPokemon +".gif";
				response.addImage(new File(path));
				builder.setFooter("Pledge $1 to receive this perk!", this.getPatreonLogo());
			}
			else
			{
				builder.setColor(colorService.getColorForPatreon());
				builder.setImage(this.getPatreonLogo());
			}
			
			//format reply
			response.addToReply("Pledge $1/month on Patreon to gain access to all HD shiny Pokemon!");
			builder.addField("Patreon link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
			builder.setThumbnail(this.getPatreonBanner());
			
			response.setEmbed(builder);
			return response;
		}
		catch (Exception e) 
		{
			this.addErrorMessage(response, input, "1012a", e); 
			return response;
		}
	}

}
