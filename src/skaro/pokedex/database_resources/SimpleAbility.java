package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A SimpleAbility object represents the most basic data for an Ability. 
 * This is for the purpose of accessing some basic data about a Ability without
 * making unnecessary database queries about every detail of an Ability. Basic
 * data includes the proper name of the Ability
 * @author Ben
 *
 */
public class SimpleAbility 
{
	private String name;
	
	/**
	 * A constructor for a SimplePokemon object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * @param basicData
	 */
	public SimpleAbility(ResultSet basicData)
	{
		try 
		{
			basicData.next();
			name = basicData.getString(2).intern();
		} 
		catch (SQLException e) 
		{
			name = null;
			System.err.println("[SimpleAbility] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getName() { return name; }
	public String toString() { return name; }
}
