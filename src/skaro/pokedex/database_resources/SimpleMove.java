package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A SimpleMove object represents the most basic data for a Move. 
 * This is for the purpose of accessing some basic data about a Move without
 * making unnecessary database queries about every detail of an Move. Basic
 * data includes the proper name of the Move and the type.
 * @author Ben
 *
 */
public class SimpleMove 
{
	private String name;
	private String type;
	
	/**
	 * A constructor for a SimplePokemon object.  If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * @param basicData
	 */
	public SimpleMove(ResultSet basicData)
	{
		try 
		{
			basicData.next();
			name = basicData.getString(2).intern();
			type = basicData.getString(12).intern();
		} 
		catch (SQLException e) 
		{
			name = null;
			System.err.println("[SimpleMove] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getName() { return name; }
	public String getType() { return type; }
	public String toString() { return name; }
}
