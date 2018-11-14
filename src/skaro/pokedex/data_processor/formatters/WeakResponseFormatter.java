package skaro.pokedex.data_processor.formatters;

import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.EmojiTracker;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class WeakResponseFormatter implements IDiscordFormatter 
{

	@Override
	public Response invalidInputResponse(Input input) 
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify 1 Pokemon or between 1 and 2 Types (seperated by commas) "
						+ "as input for this command.");
			break;
			case INVALID_ARGUMENT:
				response.addToReply("Could not process your request due to the following problem(s):".intern());
				for(AbstractArgument arg : input.getArgs())
					if(!arg.isValid())
						response.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
				response.addToReply("\n*top suggestion*: did you include commas between inputs?");
			break;
			default:
				response.addToReply("A technical error occured (code 106)");
		}
		
		return response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Language lang = input.getLanguage();
		List<TypeData> typeList = (List<TypeData>)(List<?>)data.get(TypeData.class.getName());
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		Response response = new Response();
		builder.setLenient(true);
		
		TypeData type1 = typeList.get(0);
		TypeData type2 = typeList.size() > 1 ? typeList.get(1) : null;
		TypeInteractionWrapper wrapper = TypeTracker.onDefense(type1, type2);
		
		//Add model and header depending on if the user specified a Pokemon
		if(pokemon != null && species != null)
		{
			builder.withThumbnail(pokemon.getSprites().getFrontDefault());
			response.addToReply(formatHeader(species, type1, type2, lang));
		}
		else
			response.addToReply(formatHeader(type1, type2, lang));
		
		//Format body
		builder.appendField(CommonData.WEAK.getInLanguage(lang), combineLists(wrapper, lang, 2.0, 4.0), false);
		builder.appendField(CommonData.NEUTRAL.getInLanguage(lang), getList(wrapper, lang, 1.0), false);
		builder.appendField(CommonData.RESIST.getInLanguage(lang), combineLists(wrapper, lang, 0.5, 0.25), false);
		builder.appendField(CommonData.IMMUNE.getInLanguage(lang), getList(wrapper, lang, 0.0), false);
		
		//Set color
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private String formatHeader(PokemonSpecies species, TypeData type1, TypeData type2, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("**__"+TextFormatter.pokemonFlexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" ");
		builder.append("("+ EmojiTracker.getTypeEmoji(type1) + type1.getNameInLanguage(lang));
		builder.append(type2 != null 
				? ("/"+EmojiTracker.getTypeEmoji(type2) + type2.getNameInLanguage(lang) +")__**") 
				: ")__**");
		
		return builder.toString();
	}
	
	private String formatHeader(TypeData type1, TypeData type2, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("**__" + EmojiTracker.getTypeEmoji(type1) + type1.getNameInLanguage(lang));
		builder.append(type2 != null ? "/"+ EmojiTracker.getTypeEmoji(type2) + type2.getNameInLanguage(lang) +"__**": "__**");
		
		return builder.toString();
	}
	
	private String combineLists(TypeInteractionWrapper wrapper, Language lang, double mult1, double mult2)
	{
		Optional<String> strCheck;
		String inter1, intern2;
		StringBuilder builder = new StringBuilder();
		
		strCheck = wrapper.interactionToString(mult1, lang);
		inter1 = strCheck.isPresent() ? strCheck.get() : null;
		
		strCheck = wrapper.interactionToString(mult2, lang);
		intern2 = strCheck.isPresent() ? strCheck.get() : null;
		
		if(inter1 == null && intern2 == null)
			return null;
		
		if(inter1 != null)
			builder.append(inter1);
		
		if(inter1 != null && intern2 != null)
			builder.append(", **"+intern2+"**");
		else if(intern2 != null)
			builder.append("**"+intern2+"**");
		
		return builder.toString();
	}
	
	private String getList(TypeInteractionWrapper wrapper, Language lang, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult, lang);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
	
	
}
