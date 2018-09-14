package skaro.pokedex.data_processor.formatters;

import skaro.pokeflex.objects.egg_group.EggGroup;

public class EggGroupList 
{
	EggGroup group1, group2;
	
	public EggGroupList()
	{	}
	
	public EggGroupList(EggGroup g1, EggGroup g2)
	{
		group1 = g1;
		group2 = g2;
	}

	public EggGroup getGroup1() { return group1; }
	public EggGroup getGroup2() { return group2; }
}
