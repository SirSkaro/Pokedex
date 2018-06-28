package skaro.pokedex.core;

public enum Privilege 
{
	DEVELOPER("Super Nerd (Dev)", 339585703530725377L),
	MODERATOR("Officer (Mod)", 339595208276705280L),
	SUPPORTER("Celebrity", 339585896414314496L),
	TIER1("Champion", 459843699271139331L),
	TIER2("Veteran", 459843127126130704L),
	TIER3("Ace Trainer", 459843076828037160L),
	TIER4("Youngster/Lass", 459841947238924308L),
	;
	
	private String name;
	private long id;
	
	private Privilege(String name, long id)
	{
		this.name = name;
		this.id = id;
	}
	
	public String getName() { return name; }
	public long getID() { return id; }
}
