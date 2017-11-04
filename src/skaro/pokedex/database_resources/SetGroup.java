package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A SetGroup object represents all the common Sets used for a Pokemon
 * in a particular meta game in a particular tier.
 * @author Ben
 *
 */
public class SetGroup 
{
	private String species;
	private ArrayList<Set> sets;
	private String url;
	private String tier;
	private int gen;
	
	/**
	 * A constructor for a SetGroup object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 */
	public SetGroup(ResultSet setData, ResultSet pokeData)
	{
		sets = new ArrayList<Set>();
		try
		{	
			Set tempSet;
			
			//Set Species
			pokeData.next();
			species = pokeData.getString(1).intern();
			
			//Set all the sets
			while(setData.next())
			{
				tempSet = new Set(setData);
				if(tempSet.getLevel() != -1)
					sets.add(tempSet);
			}
			
			//Set general data about these sets
			if(setData.previous())
			{
				url = setData.getString(14);
				tier = setData.getString(5).intern();
				gen = setData.getInt(4);
			}
		}
		catch (SQLException e) 
		{
			species = null;
			System.err.println("[SetGroup] SQL exception");
			e.printStackTrace();
		}
	}

	public String getSpecies() { return species; }
	public ArrayList<Set> getSets() { return sets; }
	public String getURL() { return url; }
	public String getTier() { return tier; }
	public int getGen() { return gen; }
}