package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A PokedexEntry object represents the most basic data for a Pokedex Entry. 
 * Basic data includes the proper name of the Pokemon, the Pokedex entry,
 * the Pokemon's category, and the version of the game this entry belongs to.
 * @author Ben
 *
 */
public class PokedexEntry 
{
	private String species;
	private String category;
	private String entry;
	private String version;
	
	/**
	 * A constructor for a PokedexEntry object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data. If the
	 * data member "entry" is null, then the Pokedex entry shouldn't exist.
	 * @param basicData
	 */
	public PokedexEntry(ResultSet dexData, ResultSet pokeData)
	{
		try 
		{
			dexData.next();
			version = dexData.getString(3).intern();
			category = dexData.getString(4).intern();
			entry = dexData.getString(5);
		} 
		catch(SQLException e) 
		{
			entry = null;
			System.err.println("[PokedexEntry] SQL exception");
			//e.printStackTrace();
		}
		
		try 
		{			
			pokeData.next();
			species = pokeData.getString(1).intern();
		} 
		catch(SQLException e) 
		{
			species = null;
			System.err.println("[PokedexEntry] SQL exception");
			e.printStackTrace();
		}
	}

	public String getSpecies() { return species; }
	public String getCategory() { return category; }
	public String getEntry() { return entry; }
	public String getVersion() { return version; }
}
