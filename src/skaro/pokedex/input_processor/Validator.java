package skaro.pokedex.input_processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import skaro.pokedex.data_processor.ICommand.ArgumentCategory;

public class Validator 
{
	private static Validator instance;
	private static MySQLManager sqlManager;
	private static ArrayList<String> metas, types, regions, versions;
	
	private Validator()
	{
		sqlManager = MySQLManager.getInstance();
		
		metas = new ArrayList<String>();
		metas.add("ag"); metas.add("battlespotdoubles"); metas.add("battlespotsingles");
		metas.add("doubles"); metas.add("lc"); metas.add("nu"); metas.add("uber");
		metas.add("ou"); metas.add("pu"); metas.add("ru"); metas.add("uu"); metas.add("vgc");
		
		types = new ArrayList<String>();
		types.add("normal"); types.add("fighting"); types.add("flying"); types.add("poison"); 
		types.add("ground"); types.add("rock"); types.add("bug"); types.add("water");
		types.add("ghost"); types.add("steel"); types.add("fire"); types.add("electric");
		types.add("grass"); types.add("psychic"); types.add("ice"); types.add("dragon");
		types.add("dark"); types.add("fairy"); types.add("bird");
		
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
		versions.add("ultrasun"); versions.add("ultramoon");
	}
	
	public static Validator getInstance()
	{
		if(instance == null)
			instance = new Validator();
		return instance;
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
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT pid FROM Pokemon WHERE pid = '"+s+"';");
		
		if(resultOptional.isPresent())
			try { return (resultOptional.get().next()); } 
			catch (SQLException e) { return false; }
		return false;
	}
	
	public boolean isItem(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT iid FROM Item WHERE iid = '"+s+"-i';");
		
		if(resultOptional.isPresent())
			try { return (resultOptional.get().next()); } 
			catch (SQLException e) { return false; }
		return false;
	}
	
	public boolean isMove(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT mid FROM Move WHERE mid = '"+s+"-m';");
		
		if(resultOptional.isPresent())
			try { return (resultOptional.get().next()); } 
			catch (SQLException e) { return false; }
		return false;
	}
	
	public boolean isAbility(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT aid FROM Ability WHERE aid = '"+s+"-a';");
		
		if(resultOptional.isPresent())
			try { return (resultOptional.get().next()); } 
			catch (SQLException e) { return false; }
		return false;
	}
	
	public boolean isVersion(String s)
	{
		return (versions.contains(s));
	}
	
	public boolean isMeta(String s)
	{	
		return (metas.contains(s));
	}
	
	public boolean isType(String s)
	{
		return (types.contains(s));
	}
	
	public boolean isRegion(String s)
	{
		return (regions.contains(s));
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
}
