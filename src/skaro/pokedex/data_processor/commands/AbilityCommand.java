package skaro.pokedex.data_processor.commands;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AbilityCommand extends AbstractCommand 
{	
	public AbilityCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "ability".intern();
		argCats.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new ArgumentRange(1,1);
		
		aliases.put("ab", Language.ENGLISH);
		aliases.put("abil", Language.ENGLISH);
		
		createHelpMessage("Starmie", "Flash Fire", "celebi", "natural cure",
				"https://i.imgur.com/biWBKIL.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	
	public String getArguments() { return "<pokemon> or <ability>"; }
	
	protected boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Pokemon or Ability as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon or Ability");
				break;
				default:
					reply.addToReply("A technical error occured (code 103)");
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
		
		Object flexObj;
		
		//Extract data
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			try 
			{
				//Obtain data
				flexObj = factory.createFlexObject(Endpoint.ABILITY, input.argsAsList());
				Ability abil = Ability.class.cast(flexObj);
				
				//format reply
				reply.addToReply(("**__"+TextFormatter.flexFormToProper(abil.getName())+"__**").intern());
				reply.setEmbededReply(formatEmbed(abil));
			} 
			catch (Exception e)  { this.addErrorMessage(reply, input, "1003a", e); }
		}
		else//if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
		{
			try 
			{
				//Obtain data
				flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
				Pokemon pokemon = Pokemon.class.cast(flexObj);
				
				//Format reply
				reply.addToReply(("**__"+TextFormatter.flexFormToProper(pokemon.getName())+"__**").intern());
				reply.setEmbededReply(formatEmbed(pokemon));
			}
			catch (Exception e) { this.addErrorMessage(reply, input, "1003b", e); }
		}
		
		return reply;
	}
		
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		for(int slot = 1; slot <= 4; slot++)
			for(skaro.pokeflex.objects.pokemon.Ability abil : pokemon.getAbilities())
			{
				if(slot == abil.getSlot())
				{
					builder.appendField(abil.isIsHidden() ? "Hidden Ability" : "Ability "+ slot,
							TextFormatter.flexFormToProper(abil.getAbility().getName()), true);
				}
			}
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Add thumbnail
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		
		//Add adopter
		this.addAdopter(pokemon, builder);
		
		return builder.build();
	}
	
	private EmbedObject formatEmbed(Ability abil)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.appendField("Debut", TextFormatter.formatGeneration(abil.getGeneration().getName()), true);
		builder.appendField("Smogon Viability", abil.getRating(), true);
		builder.appendField("Pokemon with this Ability", Integer.toString(abil.getPokemon().size()), true);
		builder.appendField("Game Description", abil.getSdesc(), false);
		builder.appendField("Technical Description", abil.getLdesc(), false);
		builder.withColor(ColorTracker.getColorForAbility());
		
		this.addRandomExtraMessage(builder);
		return builder.build();
	}
	
	
}