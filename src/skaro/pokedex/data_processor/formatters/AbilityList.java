package skaro.pokedex.data_processor.formatters;

import skaro.pokeflex.objects.ability.Ability;

public class AbilityList 
{
	Ability slot1, slot2, hidden;
	
	public AbilityList()
	{	}
	
	public AbilityList(Ability a1, Ability a2, Ability h)
	{
		slot1 = a1;
		slot2 = a2;
		hidden = h;
	}

	public Ability getSlot1() { return slot1; }
	public Ability getSlot2() { return slot2; }
	public Ability getHidden() { return hidden; }
	
}
