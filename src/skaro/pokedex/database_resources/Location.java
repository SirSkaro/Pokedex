package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Location object represents a location in a main-series game where a Pokemon
 * can be found in the wild. 
 * @author Ben
 *
 */
public class Location 
{
	private String region;
	private String route;
	private String method;
	private String chance;
	private String level;
	
	/**
	 * A constructor for a Location object.  If the data member "level" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * This constructor assumes that the ResultSet object is on a fresh row.
	 */
	public Location(ResultSet locData)
	{
		try 
		{
			region = locData.getString(5).intern();
			route = locData.getString(6).intern();
			method = locData.getString(7).intern();
			chance = locData.getString(8);
			level = locData.getString(9);
		} 
		catch (SQLException e) 
		{
			level = null;
			System.err.println("[Location] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getRegion() { return region; }
	public String getRoute() { return route; }
	public String getMethod() { return method; }
	public String getChance() { return chance; }
	public String getLevel() { return level; }
}