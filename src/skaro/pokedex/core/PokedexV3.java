package skaro.pokedex.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class PokedexV3 
{
	public static void main(String[] args) throws Exception
	{
		int shardIDToManage = -1;
		int totalShards = -1;
		
		//Load configurations
		System.out.println("[Pokedex main] Loading configurations...");
		ScheduledExecutorService pokedexThreadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 6);
		
		PokedexManager manager = PokedexManager.PokedexConfigurator.newInstance(ConfigurationType.DEVELOP, pokedexThreadPool)
								.buildCommandMap()
								.buildDiscordClient(totalShards, shardIDToManage)
								.buildPatreonClient()
								.buildColorService()
								.buildEmojiService()
								.initPokeFlexFactory()
								.configure();
		
		DiscordService service = (DiscordService)manager.getService(ServiceType.DISCORD);
		DiscordClient client = service.getV3Client();
		Scheduler scheduler = Schedulers.fromExecutorService(pokedexThreadPool);
		
		client.getEventDispatcher().on(MessageCreateEvent.class).publishOn(scheduler) // This listens for all events that are of MessageCreateEvent
        .subscribe(event -> event.getMessage().getContent().ifPresent(c -> System.out.println(c))); // "subscribe" is the method you need to call to actually make sure that it's doing something.

		client.login().block(); 
	}
}
