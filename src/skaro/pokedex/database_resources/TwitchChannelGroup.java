package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class TwitchChannelGroup 
{
	private ArrayList<TwitchChannel> channels;
	private Optional<Boolean> success;
	
	/**
	 * A constructor for a TwitchChannelGroup object. If the data member "success" is set to Optional.empty(),
	 * then an SQLException happened and the object cannot be trusted to contain correct data.
	 */
	public TwitchChannelGroup(ResultSet channelData)
	{
		channels = new ArrayList<TwitchChannel>();
		
		try
		{
			while(channelData.next())
				channels.add(new TwitchChannel(channelData));
			
			success = Optional.of(Boolean.TRUE);
		}
		catch (SQLException e) 
		{
			success = Optional.empty();
			System.err.println("[TwitchChannelGroup] SQL exception");
			e.printStackTrace();
		}
	}

	public ArrayList<TwitchChannel> getChannels() {	return channels; }
	public Optional<Boolean> getSuccess() { return success; }
}
