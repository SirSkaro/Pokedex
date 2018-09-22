package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.MoveResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.move.Move;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MoveCommand extends AbstractCommand 
{
	public MoveCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "move".intern();
		argCats.add(ArgumentCategory.MOVE);
		expectedArgRange = new ArgumentRange(1,1);
		formatter = new MoveResponseFormatter();
		
		aliases.put("mv", Language.ENGLISH);
		aliases.put("moves", Language.ENGLISH);
		aliases.put("attack", Language.ENGLISH);
		aliases.put("attacke", Language.GERMAN);
		aliases.put("movimiento", Language.SPANISH);
		aliases.put("capacite", Language.FRENCH);
		aliases.put("capacité", Language.FRENCH);
		aliases.put("attaque", Language.FRENCH);
		aliases.put("mossa", Language.ITALIAN);
		aliases.put("waza", Language.JAPANESE_HIR_KAT);
		aliases.put("zhāoshì", Language.CHINESE_SIMPMLIFIED);
		aliases.put("gisul", Language.KOREAN);
		
		createHelpMessage("Ember", "dragon ascent", "aeroblast", "Blast Burn",
				"https://i.imgur.com/B3VtWyg.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<move>"; }
	
	public Response discordReply(Input input, IUser requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		
		try
		{
			//Initial data - Move object
			Move move = (Move)factory.createFlexObject(Endpoint.MOVE, input.argsAsList());
			dataMap.put(Move.class.getName(), move);
			
			//Type
			concurrentRequestList.add(new RequestURL(move.getType().getUrl(), Endpoint.TYPE));
			
			//Target
			concurrentRequestList.add(new RequestURL(move.getTarget().getUrl(), Endpoint.MOVE_TARGET));
			
			//Contest
			if(move.getContestType() != null)
				concurrentRequestList.add(new RequestURL(move.getContestType().getUrl(), Endpoint.CONTEST_TYPE));
			
			//Damage Class (Category)
			concurrentRequestList.add(new RequestURL(move.getDamageClass().getUrl(), Endpoint.MOVE_DAMAGE_CLASS));
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();;
			this.addErrorMessage(response, input, "1006", e); 
			e.printStackTrace();
			return response;
		}
	}
}
