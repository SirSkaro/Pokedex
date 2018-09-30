package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import skaro.pokedex.communicator.Publisher;
import skaro.pokedex.data_processor.CommandMap;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.shard.ShardReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class PreLoginEventHandler
{
	private int statusIndex;			//count for booting tracking, statusIndex to iterate through status messages
	private CommandMap library;
	private Publisher publisher;
	private ScheduledExecutorService executor;

	public PreLoginEventHandler(CommandMap lib, Publisher pub, ScheduledExecutorService exe)
	{
		statusIndex = 0;
		library = lib;
		publisher = pub;
		executor = exe;
	}
	
	@EventSubscriber
    public void onShardReadyEvent(ShardReadyEvent event)
    {	    	    	
		IShard shard = event.getShard();
		System.out.println("[DiscordEventHandler] Shard "+shard.getInfo()[0]+" finished connecting "
				+ "with "+shard.getGuilds().size()+" guilds and "+ shard.getUsers().size()+" users.");
    }
	
    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) 
    {	    	    	
    	List<String> statusMessages = createStatusMessages();
    	PostLoginEventHandler plev;
    	IDiscordClient discordClient = event.getClient();
    	
		System.out.println("[DiscordEventHandler] Setting up publisher");
		publisher.populatePublicationRecipients(discordClient);
		publisher.scheduleHoursPerPublishment(1);
		
		System.out.println("[DiscordEventHandler] Setting up status message rotation");
		executor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() {
				try 
				{
					event.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, statusMessages.get(statusIndex % statusMessages.size()));
	            	statusIndex++;
				}
				catch(Exception e) {/*noop*/}
		}}
		, 0, 60, TimeUnit.SECONDS);
		
    	
    	System.out.println("[DiscordEventHandler] Finished logging into Discord. Trading event handlers");
    	plev = new PostLoginEventHandler(library, discordClient.getOurUser().getLongID());
    	discordClient.getDispatcher().unregisterListener(this);
    	discordClient.getDispatcher().registerListener(executor, plev);
    }
    
    private List<String> createStatusMessages()
    {
    	List<String> statusMessages = new ArrayList<String>();
		statusMessages.add("!commands/!help");
		statusMessages.add("[NEW] %shiny");
		statusMessages.add("%commands/%help");
		statusMessages.add("[NEW] %patreon");
		statusMessages.add("commands()/help()");
		statusMessages.add("%invite");
		
		return statusMessages;
    }
}
