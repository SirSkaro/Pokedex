package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.AbstractArgument;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.encounter.ConditionValue;
import skaro.pokeflex.objects.encounter.Encounter;
import skaro.pokeflex.objects.encounter.EncounterDetail;
import skaro.pokeflex.objects.encounter.EncounterPotential;
import skaro.pokeflex.objects.encounter.VersionDetail;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class LocationCommand implements ICommand 
{
	private static LocationCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private LocationCommand(PokeFlexFactory pff)
	{
		commandName = "location".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new ArgumentRange(2,2);
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new LocationCommand(pff);
		return instance;
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [version] (not updated for gen 7)";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Not updated for gen7. Try versions from gens 1-6?");
				break;
				default:
					reply.addToReply("A technical error occured (code 111)");
			}
			
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		Pokemon pokemon = null;
		Encounter encounterData = null;
		
		try 
		{
			//Obtain Pokemon data
			List<String> urlParams = new ArrayList<String>();
			urlParams.add(input.getArg(0).getFlexForm());
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			pokemon = Pokemon.class.cast(flexObj);
			
			//Extract needed data from the Pokemon data and get Encounter data
			urlParams.clear();
			urlParams.add(Integer.toString(pokemon.getId()));
			urlParams.add("encounters");
			flexObj = factory.createFlexObject(Endpoint.ENCOUNTER, urlParams);
			encounterData = Encounter.class.cast(flexObj);
		} 
		catch(IOException | PokeFlexException e)
		{ 
			this.addErrorMessage(reply, "1011", e); 
			return reply;
		}
		
		//Get encounter data from particular version
		String versionDBForm = input.getArg(1).getDbForm();
		List<EncounterPotential> encounterDataFromVersion = getEncounterDataFromVersion(encounterData, versionDBForm);
		
		if(encounterDataFromVersion.isEmpty())
		{
			reply.addToReply(TextFormatter.flexFormToProper(pokemon.getName())+" cannot be found by means of a normal encounter in "
					+ TextFormatter.flexFormToProper(input.getArg(1).getRawInput())+" version");
			return reply;
		}
		
		//Format reply
		reply.addToReply("**"+TextFormatter.flexFormToProper(pokemon.getName())+"** can be found in **"+(encounterDataFromVersion.size())+
				"** location(s) in **"+TextFormatter.flexFormToProper(versionDBForm)+"** version");
		reply.setEmbededReply(formatEmbed(encounterDataFromVersion, versionDBForm));
		
		return reply;
	}
	
	private EmbedObject formatEmbed(List<EncounterPotential> encounterDataFromVersion, String version) 
	{
		EmbedBuilder eBuilder = new EmbedBuilder();	
		StringBuilder sBuilder;
		Set<String> detailsList; 
		VersionDetail vDetails;
		eBuilder.setLenient(true);
		
		for(EncounterPotential potential : encounterDataFromVersion)
		{
			detailsList = new HashSet<String>(); 
			vDetails = getVersionDetailFromVersion(potential, version).get(); //Assume the Optional is not empty, it should have been previously checked
			
			sBuilder = new StringBuilder();
			sBuilder.append(String.format("`|Max Encounter Rate: %-5s|`\n", vDetails.getMaxChance() + "%"));
			for(EncounterDetail eDetails : vDetails.getEncounterDetails())
			{
				sBuilder.append("`|-------------------------|`\n");
				sBuilder.append(formatLevel(eDetails));
				sBuilder.append(String.format("`|Method: %-17s|`\n", TextFormatter.flexFormToProper(eDetails.getMethod().getName())));
				sBuilder.append(String.format("`|Conditions: %-13s|`\n", formatConditions(eDetails)));
				sBuilder.append(String.format("`|Encounter Rate: %-9s|`\n",eDetails.getChance() + "%"));
			}
			
			detailsList.add(sBuilder.toString());
			
			eBuilder.appendField(TextFormatter.flexFormToProper(potential.getLocationArea().getName()), sBuilder.toString(), true);
		}
		
		eBuilder.withColor(ColorTracker.getColorForVersion(version));
		return eBuilder.build();
	}
	
	private String formatLevel(EncounterDetail details)
	{
		if(details.getMaxLevel() == details.getMinLevel())
			return String.format("`|Level: %-18d|`\n", details.getMinLevel());
			
		return String.format("`|Levels: %2d/%-14d|`\n", details.getMinLevel(), details.getMaxLevel());
	}
	
	private String formatConditions(EncounterDetail details)
	{
		if(details.getConditionValues().isEmpty())
			return "None";
		
		StringBuilder builder = new StringBuilder();
		
		for(ConditionValue cond : details.getConditionValues())
			builder.append(TextFormatter.flexFormToProper(cond.getName()) + " & ");
		
		return builder.substring(0, builder.length() - 3);
	}

	private List<EncounterPotential> getEncounterDataFromVersion(Encounter encounterData, String version)
	{
		List<EncounterPotential> result = new ArrayList<EncounterPotential>();
		Optional<VersionDetail> detailCheck;
		
		if(encounterData.getEncounterPotential() == null || encounterData.getEncounterPotential().isEmpty())
			return result;
		
		for(EncounterPotential potential : encounterData.getEncounterPotential())
		{
			detailCheck = getVersionDetailFromVersion(potential, version);
			if(detailCheck.isPresent())
				result.add(potential);
		}
			
		return result;
	}
	
	private Optional<VersionDetail> getVersionDetailFromVersion(EncounterPotential potential, String version) 
	{
		for(VersionDetail vDetail : potential.getVersionDetails())
			if(TextFormatter.flexToDBForm(vDetail.getVersion().getName()).equals(version))
				return Optional.of(vDetail);
		
		return Optional.empty();
	}
}
