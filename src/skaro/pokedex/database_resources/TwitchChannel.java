package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TwitchChannel 
{
	private String channelName;
	private Optional<Boolean> success;
	
	public TwitchChannel(ResultSet basicData)
	{
		try 
		{
			this.channelName = basicData.getString(1).intern();
			
			success = Optional.of(Boolean.TRUE);
		} 
		catch (SQLException e) 
		{
			success = Optional.empty();
			System.err.println("[TwitchChannel] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getChannelName() { return this.channelName; }
	public Optional<Boolean> wasSuccessful() { return this.success; }
}
