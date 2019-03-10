package skaro.pokedex.services;

public enum PerkTier
{
	NO_PERK(Integer.MIN_VALUE, 99),
	YOUNGSTER_LASS(100, 299),
	ACE_TRAINER(300, 999),
	VETERAN(1000, 2499),
	CHAMPION(2500, Integer.MAX_VALUE),
	;
	
	private int floor;
	private int roof;
	
	private PerkTier(int floor, int roof) {
		this.floor = floor;
		this.roof = roof;
	}
	
	public int getRoof()
	{
		return roof;
	}
	
	public int getFloor()
	{
		return floor;
	}
}
