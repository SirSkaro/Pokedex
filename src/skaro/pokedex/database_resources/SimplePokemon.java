package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A SimplePokemon object represents the most basic data for a Pokemon. 
 * This is for the purpose of accessing some basic data about a Pokemon without
 * making unnecessary database queries about every detail of a Pokemon. Basic
 * data includes the species name, typing, and stats.
 * @author Ben
 *
 */
public class SimplePokemon 
{
	private String species;
	private String type1;
	private String type2;
	private int[] stats;

	/**
	 * A constructor for a SimplePokemon object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * @param basicData
	 */
	public SimplePokemon(ResultSet basicData)
	{
		String temp;
		try 
		{
			basicData.next();
			species = basicData.getString(2).intern();
			type1 = basicData.getString(4).intern();
			temp = basicData.getString(5);
				if(temp != null)
				type2 = temp.intern();
			
			stats = new int[6];
			for(int i = 8; i < 14; i++)
				stats[i-8] = basicData.getInt(i);
		} 
		catch (SQLException e) 
		{
			species = null;
			System.err.println("[SimplePokemon] SQL exception");
			e.printStackTrace();
		}
	}

	public String getSpecies() { return species; }
	public String getType1() { return type1; }
	public String getType2() { return type2; }
	public int[] getStats() { return stats; }
}
