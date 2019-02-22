package skaro.pokedex.data_processor.commands;

public class ArgumentRange 
{
	private int miniumum;
	private int maximum;
	
	ArgumentRange(int min, int max)
	{
		miniumum = min;
		maximum = max;
	}
	
	public int getMin() { return miniumum; }
	public int getMax() { return maximum; }
	
	public boolean numberInRange(int number)
	{
		return number >= miniumum && number <= maximum;
	}
}
