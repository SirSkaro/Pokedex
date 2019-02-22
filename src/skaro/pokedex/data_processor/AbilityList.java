package skaro.pokedex.data_processor;

import java.util.List;

import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;

public class AbilityList 
{
	private Ability slot1, slot2, hidden;
	
	public AbilityList()
	{	}
	
	public AbilityList(List<Ability> abilities, Pokemon pokemon)
	{
		for(skaro.pokeflex.objects.pokemon.Ability abil : pokemon.getAbilities())
		{
			switch(abil.getSlot())
			{
				case 1: 
					slot1 = getAbilityByName(abil.getAbility().getName(), abilities);
					break;
				case 2:
					slot2 = getAbilityByName(abil.getAbility().getName(), abilities);
					break;
				case 3:
					hidden = getAbilityByName(abil.getAbility().getName(), abilities);
					break;
			}
		}
	}

	public Ability getSlot1() { return slot1; }
	public Ability getSlot2() { return slot2; }
	public Ability getHidden() { return hidden; }

	public boolean hasSlot2() { return slot2 != null; }
	public boolean hasHidden() { return hidden != null; }
	
	private Ability getAbilityByName(String name, List<Ability> abilities)
	{
		for(Ability abil : abilities)
			if(abil.getName().equals(name))
				return abil;
		
		return null;
	}
}
