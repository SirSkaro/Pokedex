package skaro.pokedex.data_processor.formatters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.LearnMethodWrapper;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.move_learn_method.MoveLearnMethod;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class LearnResponseFormatter implements ResponseFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public LearnResponseFormatter(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR);
	}
	
	@Override
	public Response invalidInputResponse(Input input) 
	{
		Response response = new Response();
		
		//Because inputs that are not valid (case 2) are allowed this far, it is necessary to check if
		//the Pokemon is valid but allow other arguments to go unchecked
		if(!input.getArgument(0).isValid())
		{
			response.addToReply("\""+input.getArgument(0).getRawInput()+"\" is not a recognized Pokemon in "+input.getLanguage().getName());
			return response;
		}
		
		response.addToReply("A technical error occured (code 107)");
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		List<LearnMethodWrapper> wrappers = (List<LearnMethodWrapper>)(List<?>)data.get(LearnMethodWrapper.class.getName());
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		
		//Header
		response.addToReply("**__"+
				TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+
				" | #" + species.getId() +
				" | " + TextUtility.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		for(LearnMethodWrapper wrapper : wrappers)
		{
			if(!wrapper.isRecognized())
			{
				builder.addField(TextUtility.flexFormToProper(wrapper.getSpecifiedMove()), 
						LearnField.NOT_RECOGNIZED.getFieldTitle(lang), true);
				continue;
			}
			
			String methodText;
			
			if(!wrapper.moveIsLearnable())
				methodText = "*"+ LearnField.NOT_ABLE.getFieldTitle(lang) +"*";
			else
				methodText = "*"+ LearnField.ABLE.getFieldTitle(lang) +"*:\n"+ formatLearnMethod(wrapper.getMethods(), lang);
				
			builder.addField(TextUtility.flexFormToProper(wrapper.getMove().getNameInLanguage(lang.getFlexKey())), methodText,true);
		}
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		//Add thumbnail
		builder.setThumbnail(pokemon.getSprites().getFrontDefault());
		
		response.setEmbed(builder);
		return response;
	}
	
	private String formatLearnMethod(List<MoveLearnMethod> methods, Language lang) 
	{
		StringBuilder builder = new StringBuilder();
		List<String> methodTexts = new ArrayList<String>(); 
		String methodName;
		
		for(MoveLearnMethod method : methods)
		{
			methodName = TextUtility.flexFormToProper(method.getNameInLanguage(lang.getFlexKey()));
			
			//Add the method if there no duplicates
			if(!(methodTexts.contains(methodName)))
				methodTexts.add(methodName);
		}
		
		for(String method : methodTexts)
			builder.append("  "+method+"\n");
		
		return builder.toString();
	}
	
	private enum LearnField
	{
		ABLE("able via", "poder", "capable", "capace", "fähig", "できる", "能够", "할 수 있는"),
		NOT_ABLE("not able", "incapaz", "pas capable", "non in grado", "nicht fähig", "できない", "不能", "능력이 없다"),
		NOT_RECOGNIZED("not recognized", "no reconocido", "pas reconnu", "non riconosciuto", "nicht wiedererkannt", "認識されない", "未识别", "인식하지 못함"),
		;
		
		private Map<Language, String> titleMap;
		
		LearnField() {}
		LearnField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
		{
			titleMap = new HashMap<Language, String>();
			titleMap.put(Language.ENGLISH, english);
			titleMap.put(Language.SPANISH, spanish);
			titleMap.put(Language.FRENCH, french);
			titleMap.put(Language.ITALIAN, italian);
			titleMap.put(Language.GERMAN, german);
			titleMap.put(Language.JAPANESE_HIR_KAT, japanese);
			titleMap.put(Language.CHINESE_SIMPMLIFIED, chinese);
			titleMap.put(Language.KOREAN, korean);
		}
		
		public String getFieldTitle(Language lang)
		{
			return titleMap.get(lang);
		}
	}

}
