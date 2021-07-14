package skaro.pokedex.input_processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import skaro.pokedex.services.EnvironmentConfigurationService;

public class MySQLManager 
{
	private static Connection connection;
	private static MySQLManager instance;
	
	private MySQLManager() {
		DatabaseConfiguration config = EnvironmentConfigurationService.getDBCredentials();
		String connectionString = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false", config.url, config.database);
		try {  
			Class.forName("com.mysql.cj.jdbc.Driver");   
			connection = DriverManager.getConnection(connectionString, config.username, config.password);
		}
		catch(Exception e)
		{  
			System.err.println("Could not initialize DatabaseInterface");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static MySQLManager getInstance()
	{
		if(instance == null)
			instance = new MySQLManager();

		return instance;
	}
	
	public Optional<String> getFlexFormOfResource(SQLResource resourceType, String resource, Language lang)
	{
		String query = "SELECT flex_form FROM "+ resourceType.getTableName() + " WHERE "+lang.getSQLAttribute()+" = '"+resource+"';";
		Optional<ResultSet> resultSetCheck = executeQuery(query);
		
		if(!resultSetCheck.isPresent())
			return Optional.empty();
		
		return getFlexForm(resultSetCheck.get());
	}
	
	public boolean userIsDiscordVIP(long id)
	{
		Optional<ResultSet> dataCheck = executeQuery("SELECT user_id FROM DiscordVIP WHERE user_id = "+id+";");
		
		if(!dataCheck.isPresent())
			return false;
		
		try 
		{
			ResultSet result = dataCheck.get();
			boolean exists = result.next();
			result.close();
			return exists; 
		} 
		catch (SQLException e) { return false; }
	}
	
	public Optional<Long> getPokemonsAdopter(String pokemon)
	{
		Optional<ResultSet> dataCheck = executeQuery("SELECT user_id FROM DiscordAdopter WHERE pokemon = \""+pokemon+"\";");
		ResultSet rs;
		
		if(!dataCheck.isPresent())
			return Optional.empty();
			
		try
		{ 
			rs = dataCheck.get();
			rs.next();
			long userId = rs.getLong(1);
			rs.close();
			return Optional.of(userId); 
		} 
		catch (SQLException e) { return Optional.empty(); }
	}
	
	private Optional<ResultSet> executeQuery(String query)
	{
		try
		{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
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
			String flexForm = resultSet.getString("flex_form");
			resultSet.close();
			return Optional.ofNullable(flexForm);
		} 
		catch (SQLException e) 
		{ return Optional.empty(); }
	}
}

