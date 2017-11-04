package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A ComplexItem object represents all data for an Item. 
 * @author Ben
 *
 */
public class ComplexItem 
{
	private String name;
	private String category;
	private String techDesc;
	private String shortDesc;
	private int debut;
	private int flingPower;
	private String ngType;
	private int ngPower;
	
	public ComplexItem(ResultSet basicData)
	{
		String temp;
		try 
		{
			basicData.next();
			name = basicData.getString(2).intern();
			category = basicData.getString(3).intern();
			techDesc = basicData.getString(4);
			shortDesc = basicData.getString(5);
			debut = basicData.getInt(6);
			flingPower = basicData.getInt(7);
			temp = basicData.getString(8);
				if(temp != null)
				ngType = temp.intern();
			ngPower = basicData.getInt(9);
		} 
		catch (SQLException e) 
		{
			name = null;
			System.err.println("[ComplexItem] SQL exception");
			e.printStackTrace();
		}
	}
	
	public String getName() { return name; }
	public String getCategory() { return category; }
	public String getTechDesc() { return techDesc; }
	public String getShortDesc() { return shortDesc; }
	public int getDebut() { return debut; }
	public int getFlingPower() { return flingPower; }
	public String getNGType() { return ngType; }
	public int getNGPower() { return ngPower; }
	
	
}
