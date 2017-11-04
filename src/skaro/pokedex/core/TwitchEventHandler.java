package skaro.pokedex.core;

import java.io.IOException;

import org.jibble.pircbot.*;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TwitchCommandMap;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.InputProcessor;

public class TwitchEventHandler extends PircBot
{
	private TwitchCommandMap commandMap;			//Contains the 'cache' of commands
	@SuppressWarnings("unused")
	private TwitchClient twitchClient;
	private String channel;
	private InputProcessor processor;
	
	public TwitchEventHandler(TwitchClient client, String chan, String userName) 
	{
        this.setName(userName);
        twitchClient = client;
        commandMap = null;
        channel = chan;
        processor = null;
    }
	
	/**
	 * Set the command map
	 * @param map
	 */
	public void setCommandMap(TwitchCommandMap map)
	{
		commandMap = map;
	}
	
	/**
	 * Set the processor
	 * @param ip
	 */
	public void setInputProcessor(InputProcessor ip)
	{
		processor = ip;
	}
	
	/**
	 * Connect to Twitch "IRC"
	 * @param token
	 * @throws NickAlreadyInUseException
	 * @throws IOException
	 * @throws IrcException
	 */
	public void logIn(String token) throws NickAlreadyInUseException, IOException, IrcException
	{
		this.setVerbose(false);
		this.connect("irc.twitch.tv", 6667, token);
		this.joinChannel(channel);
	}
	
	/**
	 * A method to handle an 'on-message' event. 
	 * Replies to a chatter if they use a command
	 */
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		//Declare variables
		Response response;
		ICommand command;
		Input userInput;
		
        userInput = processor.processInput(message);
        
        if(userInput == null) //if the command doesn't exist, return
        	return;
        
        //If the message follows the syntax, find it in the command map
        command = commandMap.get(userInput.getFunction());
        
        if(command == null) //if the command isn't supported, return
        	return;
       
        //Get the reply of the command. Run a spell check on the arguments.
        response = command.twitchReply(userInput);
        
        System.out.println("[TWITCH "+hostname+"] "+ sender + ": " + message);
        
        //Send the textual reply to the user
        sendReply(channel, response);
	}
	
	public void sendReply(String channel, Response response)
	{
		sendMessage(channel, response.getTwitchTextReply());
		System.out.println("\tText response sent");
	}
}
