package skaro.pokedex.input_processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import skaro.pokedex.services.ConfigurationService;

public class MySQLManager 
{
	private static Connection con;
	private static MySQLManager instance;
	
	private MySQLManager()
	{
		try
		{  
			Optional<ConfigurationService> configurator = ConfigurationService.getInstance();
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
			con = DriverManager.getConnection("jdbc:mysql://"+dbURI+"/"+dbName+"?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false", dbUser, dbPassword);
		}
		catch(Exception e)
		{  
			System.err.println("Could not initialize DatabaseInterface");
			System.exit(1);
		}
	}
	
	public static MySQLManager getInstance()
	{
		if(instance == null)
			instance = new MySQLManager();

		return instance;
	}
	
	public Optional<ResultSet> dbQuery(String query)
	{
		Statement stmt;
		ResultSet rs;
		
		try
		{
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			return Optional.of(rs);
		}
		catch (SQLException e) 
		{ return Optional.empty(); }  
	}
	
	private Optional<String> getFlexForm(ResultSet resultSet)
	{
		try 
		{
			resultSet.next();
			return Optional.of(resultSet.getString("flex_form"));
		} 
		catch (SQLException e) 
		{ return Optional.empty(); }
	}
	
	public Optional<String> getPokemonFlexForm(String dbForm, Language lang)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Pokemon WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getTypeFlexForm(String dbForm, Language lang)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Type WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getAbilityFlexForm(String dbForm, Language lang)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Ability WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getMoveFlexForm(String dbForm, Language lang)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Move WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getItemFlexForm(String dbForm, Language lang)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Item WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getVersionFlexForm(String dbForm, Language lang) 
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Version WHERE "+lang.getSQLAttribute()+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public boolean userIsDiscordVIP(long id)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT user_id FROM DiscordVIP WHERE user_id = "+id+";");
		
		//If some error occurs, assume the data does not exist
		if(!dataCheck.isPresent())
			return false;
		
		try { return dataCheck.get().next(); } 
		catch (SQLException e) { return false; }
	}
	
	public Optional<Long> getPokemonsAdopter(String pokemon)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT user_id FROM DiscordAdopter WHERE pokemon = \""+pokemon+"\";");
		ResultSet rs;
		
		//If some error occurs, assume the data does not exist
		if(!dataCheck.isPresent())
			return Optional.empty();
			
		try
		{ 
			rs = dataCheck.get();
			rs.next();
			return Optional.of(rs.getLong(1)); 
		} 
		catch (SQLException e) { return Optional.empty(); }
	}
}
