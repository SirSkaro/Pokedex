package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A ComplexAbility object represents all data for an Ability. 
 * @author Ben
 *
 */
public class ComplexAbility 
{
	private String name;
	private int debut;
	private String techDesc;
	private String shortDesc;
	private int many;			//The number of Pokemon that can have this ability
	
	public ComplexAbility(ResultSet basicData)
	{
		try 
		{
			basicData.next();
			name = basicData.getString(2).intern();
			debut = basicData.getInt(3);
			techDesc = basicData.getString(4);
			shortDesc = basicData.getString(5);
			many = basicData.getInt(6);
		} 
		catch (SQLException e) 
		{
			name = null;
			System.err.println("[ComplexAbility] SQL exception");
			e.printStackTrace();
		}
	}

	public String getName() { return name; }
	public int getDebut() { return debut; }
	public String getTechDesc() { return techDesc; }
	public String getShortDesc() { return shortDesc; }
	public int getMany() { return many; }	
}
