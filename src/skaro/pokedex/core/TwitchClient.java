package skaro.pokedex.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import skaro.pokedex.data_processor.TwitchCommandMap;
import skaro.pokedex.input_processor.InputProcessor;

public class TwitchClient 
{
	private List<TwitchEventHandler> listeners;
	private String token;
	private String userName;
	
	public TwitchClient(String tok, String uName)
	{
		listeners = new ArrayList<TwitchEventHandler>();
		token = tok;
		userName = uName;
		
		//Hardcode channels until more people are interested in adding the bot to their channel
		//TODO Then create a table in the database and pull and insert names automatically from there
		registerListener("#sirskaro");
		registerListener("#pokeaim");
		registerListener("#chimpakt");
		registerListener("#definetlynotjohncena");
	}
	
	/**
	 * Create another instance of PircBot to listen to another Twitch chat
	 * @param channel - the name of the channel
	 */
	public void registerListener(String channel)
	{ 
		TwitchEventHandler teh = new TwitchEventHandler(this, channel, userName);
		listeners.add(teh); 
	}
	
	/**
	 * Connect every PircBot to Twitch "IRC"
	 * @throws NickAlreadyInUseException
	 * @throws IOException
	 * @throws IrcException
	 */
	public void connectToChannels() throws NickAlreadyInUseException, IOException, IrcException
	{
		for(TwitchEventHandler teh : listeners)
			teh.logIn(token);
	}
	
	/**
	 * Initialize command map for all listeners
	 * @param map - the CommandMap to assign
	 */
	public void assignCommandMap(TwitchCommandMap map)
	{
		for(TwitchEventHandler teh : listeners)
			teh.setCommandMap(map);
	}
	
	/**
	 * Initialize command map for all listeners
	 * @param map - the CommandMap to assign
	 */
	public void assignInputProcessor(InputProcessor ip)
	{
		for(TwitchEventHandler teh : listeners)
			teh.setInputProcessor(ip);
	}
}
