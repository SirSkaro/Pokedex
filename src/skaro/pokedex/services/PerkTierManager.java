package skaro.pokedex.services;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class PerkTierManager
{
	private NavigableMap<Integer, PerkTier> perkTierMap;
	
	public PerkTierManager() 
	{
		perkTierMap = new TreeMap<>();
		Arrays.stream(PerkTier.values())
			.forEach(perkTier -> perkTierMap.put(perkTier.getFloor(), perkTier));
	}
	
	public boolean isInTierOrHigher(int pledgeAmount, PerkTier tier) 
	{
		SortedMap<Integer, PerkTier> accessibleTiers = perkTierMap.headMap(pledgeAmount, true);
		return accessibleTiers.containsValue(tier);
	}
}
