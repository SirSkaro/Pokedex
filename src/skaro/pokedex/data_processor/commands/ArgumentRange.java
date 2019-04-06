package skaro.pokedex.data_processor.commands;

public class ArgumentRange 
{
	private int miniumum;
	private int maximum;
	
	public ArgumentRange(int min, int max)
	{
		miniumum = min;
		maximum = max;
	}
	
	public int getMin() { return miniumum; }
	public int getMax() { return maximum; }
	public void setMin(int min) { miniumum = min; }
	public void setMax(int max) { maximum = max; }
	
	public boolean numberInRange(int number)
	{
		return number >= miniumum && number <= maximum;
	}
}
