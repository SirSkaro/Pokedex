package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Set object represents one of the ways a Pokemon can be customized.
 * It includes information about the Pokemon's moves, abilities, stats, etc...
 * @author Ben
 *
 */
public class Set 
{
	private int level;
	private String ability;
	private String item;
	private String move1, move2, move3, move4;
	private String title;
	private String nature;
	private int[] ivs;
	private int[] evs;
	
	/**
	 * A constructor for a Set object.  If the data member "level" is set to -1, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * This constructor assumes that the ResultSet object is on a fresh row.
	 */
	public Set(ResultSet setData)
	{
		String temp;
		try 
		{
			level = setData.getInt(6);
			temp = setData.getString(7);
				if(temp != null)
				ability = temp.intern();
			temp = setData.getString(8);
				if(temp != null)
				item = temp.intern();
			move1 = setData.getString(9).intern();
			temp = setData.getString(10);
				if(temp != null)
				move2 = temp.intern();
			temp = setData.getString(11);
				if(temp != null)
				move3 = temp.intern();
			temp = setData.getString(12);
				if(temp != null)
				move4 = temp.intern();
			title = setData.getString(13).intern();
			temp = setData.getString(27);
				if(temp != null)
				nature = temp.intern();
			
			ivs = new int[6];
			for(int i = 15; i < 21; i++)
				ivs[i-15] = setData.getInt(i);
			
			evs = new int[6];
			for(int i = 21; i < 27; i++)
				evs[i-21] = setData.getInt(i);
		} 
		catch (SQLException e) 
		{
			level = -1;
			System.err.println("[Set] SQL exception");
			e.printStackTrace();
		}
	}

	public int getLevel() { return level; }
	public String getAbility() { return ability; }
	public String getItem() { return item; }
	public String getMove1() { return move1; }
	public String getMove2() { return move2; }
	public String getMove3() { return move3; }
	public String getMove4() { return move4; }
	public String getTitle() { return title; }
	public String getNature() { return nature; }
	public int[] getIvs() { return ivs; }
	public int[] getEvs() { return evs; }
	
	public String evsToString()
	{
		StringBuilder output = new StringBuilder();
		String stats[] = new String[]{" HP / "," Atk / "," Def / "," SpA / "," SpD / "," Spe  "};
		
		for(int i = 0; i < 6; i++)
			if(evs[i] > 0) 
				output.append(evs[i] + stats[i]);
		
		if(output.length() > 2)
			return output.substring(0, output.length() - 2);
		
		return null;
	}
	
	public String ivsToString()
	{
		StringBuilder output = new StringBuilder();
		String stats[] = new String[]{" HP / "," Atk / "," Def / "," SpA / "," SpD / "," Spe  "};
		
		for(int i = 0; i < 6; i++)
			if(ivs[i] > 0 && ivs[i] < 31) 
				output.append(ivs[i] + stats[i]);
		
		if(output.length() > 2)
			return output.substring(0, output.length() - 2);
		
		return null;
	}
}
