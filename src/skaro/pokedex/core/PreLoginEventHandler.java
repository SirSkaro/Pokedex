package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import skaro.pokedex.communicator.Publisher;
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
	private CommandLibrary library;
	private Publisher publisher;

	public PreLoginEventHandler(CommandLibrary lib, Publisher pub)
	{
		statusIndex = 0;
		library = lib;
		publisher = pub;
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
    	Timer statusTimer;					
    	TimerTask statusTask;
    	PostLoginEventHandler plev;
    	IDiscordClient discordClient = event.getClient();
    	
		System.out.println("[DiscordEventHandler] Setting up publisher");
		publisher.populatePublicationRecipients(discordClient);
		publisher.scheduleHoursPerPublishment(1);
		
		System.out.println("[DiscordEventHandler] Setting up status message rotation");
    	statusTimer = new Timer(true);
		statusTask = new TimerTask() {
            @Override
            public void run() 
            {
            	event.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, statusMessages.get(statusIndex % statusMessages.size()));
            	statusIndex++;
            }
        };	
    	
    	statusTimer.scheduleAtFixedRate(statusTask, 1000, 1 * 60 * 1000); //1 minute
    	
    	System.out.println("[DiscordEventHandler] Finished logging into Discord. Trading event handlers");
    	plev = new PostLoginEventHandler(library, discordClient.getOurUser().getLongID());
    	discordClient.getDispatcher().unregisterListener(this);
    	discordClient.getDispatcher().registerListener(plev);
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
