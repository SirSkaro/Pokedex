package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The Pokemon class is a temporary container that holds all basic
 * data about a Pokemon.
 * @author Ben
 *
 */
public class ComplexPokemon 
{
	private String species;
	private int dexNum;
	private String type1, type2;
	private float height, weight;
	private int[] stats;
	
	private ArrayList<SimpleAbility> abilities;
	private ArrayList<String> eggGroups;
	private Rational maleRatio, femaleRatio;
	private ArrayList<String> evolutions;
	private int evoLevel;
	private String model, shinyModel;

	/**
	 * A constructor for a Pokemon object. If the data member "species" is set to null, then
	 * an SQLException happened and the object cannot be trusted to contain correct data.
	 * @param basicData
	 */
	public ComplexPokemon(ResultSet basicData, ResultSet abilityData, ResultSet maleData, ResultSet femaleData,
			ResultSet eggGroup, ResultSet evoData, ResultSet modelData)
	{
		String temp;
		try 
		{
			DatabaseInterface dbi = DatabaseInterface.getInstance();
			
			//Standard Pokemon data
			basicData.next();
			species = basicData.getString(2).intern();
			dexNum = basicData.getInt(3);
			type1 = basicData.getString(4).intern();
			temp = basicData.getString(5);
				if(temp != null)
				type2 =temp.intern();
			height = basicData.getFloat(6);
			weight = basicData.getFloat(7);
			stats = new int[6];
			for(int i = 8; i < 14; i++)
				stats[i-8] = basicData.getInt(i);
			
			//Ability data
			abilities = new ArrayList<SimpleAbility>();
			while(abilityData.next())
				abilities.add(dbi.extractSimpleAbilFromDB(abilityData.getString(1)));
			
			//Gender data
			maleRatio = new Rational(maleData);
			femaleRatio = new Rational(femaleData);
			
			//Egg Group data
			eggGroups = new ArrayList<String>();
			while(eggGroup.next())
				eggGroups.add(eggGroup.getString(2).intern());
			
			//Evolution data
			SimplePokemon postEvo;
			if(evoData.next())
			{
				evoLevel = evoData.getInt(3);
				evolutions = new ArrayList<String>();
				do 
				{
					postEvo = dbi.extractSimplePokeFromDB(evoData.getString(2).intern());
					evolutions.add(postEvo.getSpecies());
				}while(evoData.next());
			}
			
			//Model URL
			modelData.next();
			shinyModel = modelData.getString(1).intern();
			if(modelData.next())
				model = modelData.getString(1).intern();
		} 
		catch (SQLException e) 
		{
			species = null;
			System.err.println("[ComplexPokemon] SQL exception");
			e.printStackTrace();
		}
	}

	public String getSpecies() { return species; }
	public int getDexNum() { return dexNum; }
	public String getType1() { return type1; }
	public String getType2() { return type2; }
	public float getHeight() { return height; }
	public float getWeight() { return weight; }
	public int[] getStats() { return stats; }
	public ArrayList<SimpleAbility> getAbilities() { return abilities; }
	public ArrayList<String> getEggGroups() { return eggGroups; }
	public ArrayList<String> getEvolutions() { return evolutions; }
	public String getMaleRatio() { return maleRatio.toString(); }
	public String getFemaleRatio() { return femaleRatio.toString(); }
	public String getModel() { return model; }
	public String getShinyModel() { return shinyModel; }

	public String getDiscordGenderRatio()
	{
		if(!maleRatio.isZero() && !femaleRatio.isZero())
			return (maleRatio.toString() + " ♂ */* " + femaleRatio.toString() + " ♀").intern();
		else if(!maleRatio.isZero() && femaleRatio.isZero())
			return "M";
		else if(!femaleRatio.isZero() && maleRatio.isZero())
			return "F";
		else
			return "N";
	}
	
	public String getTwitchGenderRatio()
	{
		if(!maleRatio.isZero() && !femaleRatio.isZero())
			return (maleRatio.toString() + " ♂/" + femaleRatio.toString() + " ♀").intern();
		else if(!maleRatio.isZero() && femaleRatio.isZero())
			return "M";
		else if(!femaleRatio.isZero() && maleRatio.isZero())
			return "F";
		else
			return "N";
	}
	
	public String getEvoLevel()
	{
		if(evoLevel > 2)
			return Integer.toString(evoLevel);
		else if (evoLevel == 1)
			return "Use of item (any level)";
		else
			return "Via friendship (level 2 and up)";
	}
	
	/**
	 * A helper class to organize gender ratios
	 */
	private class Rational 
	{
		private int num, denom;

		private Rational(ResultSet rs)
		{
			try
			{
				//Set numerator
				rs.next();
				int currNum = rs.getInt(2);
				boolean isNumerator = rs.getBoolean(3);
				if(isNumerator)
					num = currNum;
				else
					denom = currNum;
				
				//Set denominator
				rs.next();
				currNum = rs.getInt(2);
				if(!isNumerator)
					num = currNum;
				else
					denom = currNum;
			}
			catch(SQLException e)
			{
				System.err.println("[Rational(ComplexPokexmon] Unable to extract gender ratio.");
				System.exit(1);
			}
		}
		
		public String toString() { return (num + "/" + denom).intern(); }
		 
		private boolean isZero() { return num == 0; }
	}
}


