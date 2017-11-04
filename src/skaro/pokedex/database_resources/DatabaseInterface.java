package skaro.pokedex.database_resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.ICommand.ArgumentCategory;

/**
 * A Singleton class that governs the connection to the database
 * containing all information users might inquire.
 *
 */

public class DatabaseInterface 
{
	private static DatabaseInterface instance;
	private static Connection con;
	private static ArrayList<String> metas, types, regions, versions;
	
	private DatabaseInterface()
	{
		try
		{  
			Optional<Configurator> configurator = Configurator.getInstance();
			String dbPassword = null, dbName = null, dbUser = null, dbURI = null;
			if(configurator.isPresent())
			{
				String[] dbdata = configurator.get().getDBCredentials();
				dbUser = dbdata[0];
				dbName = dbdata[1];
				dbPassword = dbdata[2];
				dbURI = dbdata[3];
			}
			
			Class.forName("com.mysql.jdbc.Driver");   
			con = DriverManager.getConnection("jdbc:mysql://"+dbURI+"/"+dbName+"?autoReconnect=true&useSSL=false", dbUser, dbPassword);
		}
		catch(Exception e)
		{  
			fatalMessage("Could not initialize DatabaseInterface",e,2);
			System.exit(1);
		}
		
		metas = new ArrayList<String>();
		metas.add("ag"); metas.add("battlespotdoubles"); metas.add("battlespotsingles");
		metas.add("doubles"); metas.add("lc"); metas.add("nu"); metas.add("uber");
		metas.add("ou"); metas.add("pu"); metas.add("ru"); metas.add("uu"); metas.add("vgc");
		
		types = new ArrayList<String>();
		types.add("normal"); types.add("fighting"); types.add("flying"); types.add("poison"); 
		types.add("ground"); types.add("rock"); types.add("bug"); types.add("water");
		types.add("ghost"); types.add("steel"); types.add("fire"); types.add("electric");
		types.add("grass"); types.add("psychic"); types.add("ice"); types.add("dragon");
		types.add("dark"); types.add("fairy");
		
		regions = new ArrayList<String>();
		regions.add("kanto"); regions.add("johto"); regions.add("hoenn"); regions.add("sinnoh");
		regions.add("unova"); regions.add("kalos"); regions.add("alola");
		
		versions = new ArrayList<String>();
		versions.add("red"); versions.add("blue"); versions.add("gold"); versions.add("silver");
		versions.add("crystal"); versions.add("ruby"); versions.add("sapphire");
		versions.add("emerald"); versions.add("leafgreen"); versions.add("firered"); versions.add("diamond");
		versions.add("pearl"); versions.add("platinum"); versions.add("black");
		versions.add("black2"); versions.add("white"); versions.add("white2"); versions.add("heartgold");
		versions.add("soulsilver"); versions.add("x"); versions.add("y");
		versions.add("omegaruby"); versions.add("alphasapphire"); versions.add("sun"); versions.add("moon");
	}
	
	public static DatabaseInterface getInstance()
	{
		if(instance == null)
		{
			try 
			{
				instance = new DatabaseInterface();
			} 
			catch (Exception e) 
			{
				fatalMessage("Could not get instance of DatabaseInterface",e,2);
				System.exit(1);
			}
		}
		return instance;
	}
	
	private ResultSet dbQuery(String query)
	{
		Statement stmt;
		ResultSet rs;
		
		//System.out.println(query);
		try
		{
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			return rs;
		}
		catch (SQLException e) 
		{ 
			fatalMessage("Unable to execute query ["+query+"]. Exiting...",e,3); 
		}  
		
		return null;
	}
	
	public int resultSetSize(String query)
	{
		try
		{
			return dbQuery("SELECT COUNT(*) FROM "+query).getInt(1);
		}
		catch (SQLException e) 
		{ 
			fatalMessage("Unable to execute query ["+query+"]. Exiting...",e,9); 
			return 0;
		}
	}
	
	public int resultSetSize(ResultSet rs)
	{
		int result = 0;
		try
		{
			rs.last();
			result = rs.getRow();
			rs.first();
			rs.previous();
			return result;
		}
		catch (SQLException e) 
		{ 
			fatalMessage("Unable to count size of ResultSet. Exiting...",e,10); 
			return 0;
		}
	}
	
	/**
	 * A method to check if some resource exists in the database
	 * @param ac - the category of the resource. The category must be atomic (not a list or option selection)
	 * @param resource - the name of the resource in db format
	 * @return true if the resource is found. False otherwise
	 */
	public boolean resourceExists(ArgumentCategory ac, String resource)
	{
		switch(ac)
		{
			case ABILITY:
				return isAbility(resource);
			case GEN:
				return isGen(resource);
			case ITEM:
				return isItem(resource);
			case META:
				return isMeta(resource);
			case MOVE:
				return isMove(resource);
			case NONE:
				return true;
			case POKEMON:
				return isPokemon(resource);
			case TYPE:
				return isType(resource);
			case VERSION:
				return isVersion(resource);
			default:
				return false;
		}
	}
	
	public boolean isPokemon(String s)
	{
		ResultSet rs = dbQuery("SELECT pid FROM Pokemon WHERE pid = '"+s+"';");
		try 
		{
			return (rs.next());
		} 
		catch (SQLException e) 
		{
			fatalMessage("Unable to check Pokemon resource",e,4);
			return false;
		}
	}
	
	public boolean isItem(String s)
	{
		ResultSet rs = dbQuery("SELECT iid FROM Item WHERE iid = '"+s+"-i';");
		try 
		{
			return (rs.next());
		} 
		catch (SQLException e) 
		{
			fatalMessage("Unable to check Item resource",e,5);
			return false;
		}
	}
	
	public boolean isAbility(String s)
	{
		ResultSet rs = dbQuery("SELECT aid FROM Ability WHERE aid = '"+s+"-a';");
		try 
		{
			return (rs.next());
		} 
		catch (SQLException e) 
		{
			fatalMessage("Unable to check Ability resource",e,6);
			return false;
		}
	}
	
	public boolean isMove(String s)
	{
		ResultSet rs = dbQuery("SELECT mid FROM Move WHERE mid = '"+s+"-m';");
		try 
		{
			return (rs.next());
		} 
		catch (SQLException e) 
		{
			fatalMessage("Unable to check Move resource",e,7);
			return false;
		}
	}
	
	public boolean isVersion(String s)
	{
		return (versions.contains(dbFormat(s)));
	}
	
	public boolean isMeta(String s)
	{	
		return (metas.contains(dbFormat(s)));
	}
	
	public boolean isType(String s)
	{
		return (types.contains(dbFormat(s)));
	}
	
	public boolean isRegion(String s)
	{
		return (regions.contains(dbFormat(s)));
	}
	
	public boolean isGen(String s)
	{
		s = s.trim().replaceAll("[^0-9]", "");
		if(s.length() != 1)
			return false;
		
        char c = s.charAt(0);
        if (c < '1' || c > '6') 
        	return false;
        
        return true;
	}
	
	/**
	 * Extracts all information about a Pokemon from the database
	 * @param arg - The name of the Pokemon in data base format
	 * @return  - a ComplexPokemon object representing that Pokemon
	 */
	public ComplexPokemon extractComplexPokeFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Pokemon WHERE pid = '"+arg+"';");
		ResultSet abilityData = dbQuery("SELECT aid FROM AbilPool WHERE pid = '"+arg+"' ORDER BY anum;");
		ResultSet maleRatio = dbQuery("SELECT * FROM MaleRatio WHERE pid = '"+arg+"';");
		ResultSet femaleRatio = dbQuery("SELECT * FROM FemaleRatio WHERE pid = '"+arg+"';");
		ResultSet eggGroup = dbQuery("SELECT * FROM EggGroup WHERE pid = '"+arg+"';");
		ResultSet evoData = dbQuery("SELECT * FROM Evo WHERE pre = '"+arg+"';");
		ResultSet modelData = dbQuery("SELECT url FROM Model WHERE pid = '"+arg+"';");
		
		return new ComplexPokemon(basicData, abilityData, maleRatio, femaleRatio, eggGroup, evoData, modelData);
	}
	
	/**
	 * Extracts all information about a Pokemon from the database
	 * @param arg - The name of the Pokemon in data base format
	 * @return  - a ComplexPokemon object representing that Pokemon
	 */
	public ComplexPokemon extractRandomComplexPokeFromDB()
	{
		String pid = null;
		
		ResultSet basicData = dbQuery("SELECT * FROM Pokemon ORDER BY RAND() LIMIT 1;");
		
		try 
		{
			basicData.first();
			pid = basicData.getString(1);
			basicData.previous();
		} 
		catch (SQLException e) 
		{
			fatalMessage("Could not get random Pokemon from database.",e,11);
			System.exit(1);
		}
		
		ResultSet abilityData = dbQuery("SELECT aid FROM AbilPool WHERE pid = '"+pid+"' ORDER BY anum;");
		ResultSet maleRatio = dbQuery("SELECT * FROM MaleRatio WHERE pid = '"+pid+"';");
		ResultSet femaleRatio = dbQuery("SELECT * FROM FemaleRatio WHERE pid = '"+pid+"';");
		ResultSet eggGroup = dbQuery("SELECT * FROM EggGroup WHERE pid = '"+pid+"';");
		ResultSet evoData = dbQuery("SELECT * FROM Evo WHERE pre = '"+pid+"';");
		ResultSet modelData = dbQuery("SELECT url FROM Model WHERE pid = '"+pid+"';");
		
		return new ComplexPokemon(basicData, abilityData, maleRatio, femaleRatio, eggGroup, evoData, modelData);
	}
	
	/**
	 * Extracts some information about a Pokemon from the database
	 * @param arg - The name of the Pokemon in data base format
	 * @return  - a SimplePokemon object representing that Pokemon
	 */
	public SimplePokemon extractSimplePokeFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Pokemon WHERE pid = '"+arg+"';");
		return new SimplePokemon(basicData);
	}
	
	/**
	 * Extracts some information about an Ability from the database
	 * @param arg - The name of the Ability in data base format
	 * @return  - an SimpleAbility object representing that Ability
	 */
	public SimpleAbility extractSimpleAbilFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Ability WHERE aid = '"+arg+"';");
		return new SimpleAbility(basicData);
	}
	
	/**
	 * Extracts all information about an Ability from the database
	 * @param arg - The name of the Ability in data base format
	 * @return  - an ComplexAbility object representing that Ability
	 */
	public ComplexAbility extractComplexAbilFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Ability WHERE aid = '"+arg+"';");
		return new ComplexAbility(basicData);
	}
	
	/**
	 * Extracts all information about an Item from the database
	 * @param arg - The name of the Item in data base format
	 * @return  - an ComplexItem object representing that Item
	 */
	public ComplexItem extractComplexItemFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Item WHERE iid = '"+arg+"';");
		return new ComplexItem(basicData);
	}
	
	/**
	 * Extracts all information about a Move from the database
	 * @param arg - The name of the Move in data base format
	 * @return  - an ComplexMove object representing that Move
	 */
	public ComplexMove extractComplexMoveFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Move WHERE mid = '"+arg+"';");
		ResultSet flagData = dbQuery("SELECT flag FROM MoveFlags WHERE mid = '"+arg+"';");
		return new ComplexMove(basicData, flagData);
	}
	
	/**
	 * Extracts some information about an Move from the database
	 * @param arg - The name of the Move in data base format
	 * @return  - an SimpleMove object representing that Move
	 */
	public SimpleMove extractSimpleMoveFromDB(String arg)
	{
		ResultSet basicData = dbQuery("SELECT * FROM Move WHERE mid = '"+arg+"';");
		return new SimpleMove(basicData);
	}
	
	/**
	 * A method to check if a Move is in a Pokemon's MovePool
	 * @param move - The name of the Move in data base format
	 * @param poke - The name of the Pokemon in data base format
	 * @return  - boolean value
	 */
	public boolean inMoveSet(String move, String poke)
	{
		ResultSet basicData = dbQuery("SELECT mid FROM MovePool WHERE mid = '"+move+"' AND pid = '"+poke+"';");
		try 
		{
			return basicData.next();
		} 
		catch (SQLException e) 
		{
			fatalMessage("Could not check MovePool",e,11);
			return false;
		}
	}
	
	/**
	 * Extracts Pokedex data about a Pokemon from a specified version
	 * @param poke - The name of the Pokemon in data base format
	 * @param version - The name of the version in data base format
	 * @return  - an PokedexEntry object representing this Pokemon's Pokedex entry for the specified version
	 */
	public PokedexEntry extractDexEntryFromDB(String poke, String version)
	{
		ResultSet dexData = dbQuery("SELECT * FROM DexEntry WHERE pid = '"+poke+"' AND vid = '"+version+"';");
		ResultSet pokeData = dbQuery("SELECT specie FROM Pokemon WHERE pid = '"+poke+"';");
		return new PokedexEntry(dexData, pokeData);
	}
	
	/**
	 * Extracts sets for a specific Pokemon
	 * @param poke - The name of the Pokemon in data base format
	 * @param tier - The desired meta game in data base format
	 * @param gen - the generation the meta game was relevant in
	 * @return  - an SetGroup object representing this Pokemon's possible sets
	 */
	public SetGroup extractSetsFromDB(String poke, String tier, int gen)
	{
		ResultSet setData = dbQuery("SELECT * FROM Trick WHERE pid = '"+poke+"'"
				+ " AND tier_id = '"+tier+"'"
				+ " AND gen = "+gen+";");
		ResultSet pokeData = dbQuery("SELECT specie FROM Pokemon WHERE pid = '"+poke+"';");
		
		return new SetGroup(setData, pokeData);
	}
	
	/**
	 * Extracts Locations for a specific Pokemon in a specified version
	 * @param poke - The name of the Pokemon in data base format
	 * @param version - The desired version in data base format
	 * @return  - an Location object representing this Pokemon's possible sets
	 */
	public LocationGroup extractLocationFromDB(String poke, String version)
	{
		ResultSet locData = dbQuery("SELECT * FROM Location WHERE pid = '"+poke+"'"
				+ " AND vid = '"+version+"';");
		ResultSet pokeData = dbQuery("SELECT specie FROM Pokemon WHERE pid = '"+poke+"';");
		
		return new LocationGroup(locData, pokeData);
	}
	
	/**
	 * Takes in a name and returns it in the formatting used
	 * in the data base. No spaces or symbols.
	 * @param s - pokemon name
	 * @return formatted String
	 */
	public String dbFormat(String s)
	{
		if(s == null)
			return "";
		
		s = s.toLowerCase();
		
		//Check prefixes
		if(s.contains("primal ") 
				|| (s.contains("mega ") && !s.contains("omega "))
				|| (s.contains("alola ") || s.contains("alolan ")) )
		{
			s = s.replace("alolan", "alola");
			String[] name = s.split(" ");
			return (name[1]+name[0]+((name.length == 3) ? name[2] : ""));
		}
		
		//Check for other symbols
		if(s.endsWith("♀"))
			s = s.replace("♀", "f");
		
		if(s.endsWith("♂"))
			s = s.replace("♂", "m");
		
		if(s.contains("-"))
			s = s.replace("-", "");
		
		if(s.contains("."))
			s = s.replace(".", "");
		
		if(s.contains(" "))
			s = s.replace(" ", "");
		
		if(s.contains(","))
			s = s.replace(",", "");
		
		if(s.contains(":"))
			s = s.replace(":", "");
		
		if(s.contains("%"))
			s = s.replace("%", "");
		
		if(s.contains("alolan"))
			s = s.replace("alolan", "alola");
		
		//Remove characters that would cause an SQL exception
		if(s.contains("\\"))
			s = s.replace("\\", "");
		if(s.contains("\""))
			s = s.replace("\"", "");
		if(s.contains("'"))
			s = s.replace("'", "");
		
		return s.intern();
	}
	
	private static void fatalMessage(String fatal, Exception e, int error)
	{
		System.err.println("[DatabaseInterface] FATAL ERROR ("+error+"): "+fatal);
		System.err.println(e == null ? "Not a runtime error" : e);
		for(String trace : Arrays.toString(Thread.currentThread().getStackTrace()).split(","))
			System.err.println(trace);
		//System.exit(1);
	}
}
