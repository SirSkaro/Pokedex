package skaro.pokedex.input_processor;

public enum SQLResource
{
	POKEMON("Pokemon"),
	TYPE("Type"),
	ABILITY("Ability"),
	MOVE("Move"),
	ITEM("Item"),
	VERSION("Version"),
	ZMOVE("ZMove"),
	;

	private String tableName;
	
	private SQLResource(String tableName)
	{
		this.tableName = tableName;
	}
	
	public String getTableName()
	{
		return this.tableName;
	}
	
}
