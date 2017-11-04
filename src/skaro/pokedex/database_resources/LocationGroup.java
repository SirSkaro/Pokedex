package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LocationGroup 
{
	private String species;
	private String version;
	private ArrayList<Location> locations;
	
	/**
	 * A constructor for a SetGroup object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 */
	public LocationGroup(ResultSet locData, ResultSet pokeData)
	{
		locations = new ArrayList<Location>();
		try
		{	
			Location tempLoc;
			
			//Set Species
			pokeData.next();
			species = pokeData.getString(1).intern();
			
			//Set all the locations
			while(locData.next())
			{
				tempLoc = new Location(locData);
				if(tempLoc.getLevel() != null)
					locations.add(tempLoc);
			}
			
			//Set general data about these locations
			if(locData.previous())
			{
				version = locData.getString(4).intern();
			}
		}
		catch (SQLException e) 
		{
			species = null;
			System.err.println("[LocationGroup] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getSpecies() { return species; }
	public String getVersion() { return version; }
	public ArrayList<Location> getLocations() { return locations; }
}
