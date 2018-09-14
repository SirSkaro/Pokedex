package skaro.pokedex.input_processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import skaro.pokedex.core.Configurator;

public class MySQLManager 
{
	private static Connection con;
	private static MySQLManager instance;
	
	private MySQLManager()
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
		String attribute = lang == Language.ENGLISH ? "pid" : lang.getSQLAttribute();
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Pokemon WHERE "+attribute+" = '"+dbForm+"';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getAbilityFlexForm(String dbForm)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Ability WHERE aid = '"+dbForm+"-a';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getMoveFlexForm(String dbForm)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Move WHERE mid = '"+dbForm+"-m';");
		
		if(!dataCheck.isPresent())
			return Optional.empty();
		return getFlexForm(dataCheck.get());
	}
	
	public Optional<String> getItemFlexForm(String dbForm)
	{
		Optional<ResultSet> dataCheck = dbQuery("SELECT flex_form FROM Item WHERE iid = '"+dbForm+"-i';");
		
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
