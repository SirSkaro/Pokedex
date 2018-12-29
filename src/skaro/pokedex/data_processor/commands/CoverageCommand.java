package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.move.Move;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CoverageCommand extends AbstractCommand 
{
	public CoverageCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "coverage".intern();
		argCats.add(ArgumentCategory.MOVE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,4);
		aliases.put("strong", Language.ENGLISH);
		aliases.put("cov", Language.ENGLISH);
		aliases.put("effective", Language.ENGLISH);
		aliases.put("yuhyohan", Language.KOREAN);
		aliases.put("eficaz", Language.SPANISH);
		aliases.put("efficace", Language.FRENCH);
		aliases.put("forte", Language.ITALIAN);
		aliases.put("yǒuxiào", Language.CHINESE_SIMPMLIFIED);
		aliases.put("youxiao", Language.CHINESE_SIMPMLIFIED);
		aliases.put("efekuto", Language.JAPANESE_HIR_KAT);
		aliases.put("wirksam", Language.GERMAN);
		
		aliases.put("有效", Language.CHINESE_SIMPMLIFIED);
		aliases.put("エフェクト", Language.JAPANESE_HIR_KAT);
		aliases.put("유효한", Language.KOREAN);
		
		extraMessages.add("You may also like the %weak command!");
		
		createHelpMessage("ice, electric", "blizzard, thunder", "Ghost, Fire, Vine Whip, Hyper Beam", "Water",
				"https://i.imgur.com/MLIpXYN.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<type/move>,...,<type/move>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX);
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		PokeFlexFactory factory;
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		List<PokeFlexRequest> concurrentMoveRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		Request request;
		
		try
		{
			factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			//Sort between Move and Type arguments
			for(AbstractArgument arg : input.getArgs())
			{
				if(arg.getCategory() == ArgumentCategory.TYPE)
					dataMap.add(TypeData.class.getName(), TypeData.getByName(arg.getFlexForm()));
				else	//Category is ArgumentCategory.MOVE
				{
					request = new Request(Endpoint.MOVE);
					request.addParam(arg.getFlexForm());
					concurrentMoveRequestList.add(request);
				}
			}
			
			//Get the Types of all Moves
			if(!concurrentMoveRequestList.isEmpty())
			{
				flexData = factory.createFlexObjects(concurrentMoveRequestList);
				for(Object move : flexData)
				{
					String type = ((Move)move).getType().getName();
					dataMap.add(TypeData.class.getName(), TypeData.getByName(type));
				}
			}
			
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1007", e); 
			return response;
		}
	}
	
}