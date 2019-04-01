package skaro.pokedex.data_processor.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class SearchCommand extends PokedexCommand
{

	public SearchCommand(IServiceManager serviceManager, ResponseFormatter discordFormatter) throws ServiceConsumerException
	{
		super(serviceManager, discordFormatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
	
		commandName = "search".intern();
		//orderedArgumentCategories.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new ArgumentRange(1,1);
		
		createHelpMessage("", "", "", "",
				"");
	}

	@Override
	public boolean makesWebRequest() { return true; }

	@Override
	public String getArguments()
	{
		return "list of <moves> (max of 4) or <abilities> (max of 3) or <types> (max of 2)";
	}

	@Override
	public Mono<Response> respondTo(Input input, User author, Guild guild)
	{
		return null;
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX);
	}

}
