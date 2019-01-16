package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import skaro.pokedex.core.EmojiService;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.type.Type;

public class TypeEfficacyWrapper 
{
	private Map<Efficacy, List<Type>> interactions;
	private List<Type> typesToCheck;
	private List<Type> typesToCheckAgainst;
	
	public final double[][] efficacyMatrix = new double[/*attacker*/][/*defender*/]{
		  //  			   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17	 18
		  /*0Normal*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 0.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
		  /*1Fighting*/ { 2.0, 1.0, 0.5, 0.5, 1.0, 2.0, 0.5, 0.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 2.0, 0.5, 1.0 },
		  /*2Flying*/  	{ 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
		  /*3Poison*/  	{ 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 0.5, 0.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0 },
		  /*4Ground*/  	{ 1.0, 1.0, 0.0, 2.0, 1.0, 2.0, 0.5, 1.0, 2.0, 2.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
		  /*5Rock*/  	{ 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 2.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0 },
		  /*6Bug*/  	{ 1.0, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 2.0, 0.5, 1.0 },
		  /*7Ghost*/  	{ 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 1.0, 1.0 },
		  /*8Steel*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 0.5, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0 },
		  /*9Fire*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 2.0, 0.5, 0.5, 2.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0 },
		  /*10Water*/  	{ 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
		  /*11Grass*/  	{ 1.0, 1.0, 0.5, 0.5, 2.0, 2.0, 0.5, 1.0, 0.5, 0.5, 2.0, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
		  /*12Electric*/{ 1.0, 1.0, 2.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
		  /*13Psychic*/ { 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 0.0, 1.0, 1.0 },
		  /*14Ice*/     { 1.0, 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 2.0, 1.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0 },
		  /*15Dragon*/  { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.0, 1.0 },
		  /*16Dark*/ 	{ 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 1.0 },
		  /*17Fairy*/  	{ 1.0, 2.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0 },
		  /*18Bird*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
		    			};
	
	private TypeEfficacyWrapper(EfficacyInteractionBuilder builder)
	{
		interactions = new HashMap<>();
		typesToCheck = builder.typesToCheck;
		typesToCheckAgainst = builder.typesToCheckAgainst;
		
		for(Efficacy efficacy: Efficacy.values())
			interactions.put(efficacy, new ArrayList<Type>());
	}
	
	
	public List<Type> getInteraction(Efficacy efficacy) 
	{ 
		return new ArrayList<Type>(interactions.get(efficacy)); 
	}
	
	public List<Type> getTypes()
	{
		return new ArrayList<Type>(typesToCheck);
	}
	
	public String typesToString(Language lang, EmojiService emojiService)
	{
		StringBuilder builder = new StringBuilder();
		
		for(Type type : typesToCheck)
			builder.append("/"+emojiService.getTypeEmoji(type.getName()) + type.getNameInLanguage(lang.getFlexKey()));
		
		return builder.substring(1);
	}
	
	public Optional<String> interactionToString(Efficacy efficacy, Language lang)
	{
		if(interactions.get(efficacy).isEmpty())
			return Optional.empty();
		
		StringBuilder builder = new StringBuilder();
		
		for(Type type : interactions.get(efficacy))
			builder.append(", "+type.getNameInLanguage(lang.getFlexKey()));
		
		return Optional.of(builder.substring(2));
	}
	
	public static class EfficacyInteractionBuilder
	{
		private List<Type> typesToCheck;
		private List<Type> typesToCheckAgainst;
		private EfficacyOption efficacyOption;
		
		private EfficacyInteractionBuilder() 
		{
			typesToCheck = new ArrayList<>();
		}
		
		public static EfficacyInteractionBuilder newInstance(EfficacyOption option)
		{
			EfficacyInteractionBuilder builder = new EfficacyInteractionBuilder();
			builder.efficacyOption = option;
			return builder;
		}
		
		public EfficacyInteractionBuilder addTypesToCheckAgainst(List<Type> types)
		{
			typesToCheckAgainst = types;
			return this;
		}
		
		public EfficacyInteractionBuilder addType(Type type)
		{
			typesToCheck.add(type);
			return this;
		}
		
		public TypeEfficacyWrapper build()
		{
			if(typesToCheck.isEmpty())
				throw new EfficacyBuilderException("Must specify at least one type");
			
			TypeEfficacyWrapper result = new TypeEfficacyWrapper(this);
			
			if(efficacyOption == EfficacyOption.OFFENSE)
				result.setUpOnOffense();
			else
				result.setUpOnDefense();
			
			return result;
		}
	}
	
	public enum EfficacyOption
	{
		OFFENSE,
		DEFENSE,
		;
	}
	
	public enum Efficacy
	{
		IMMUNE(0.0),
		QUAD_RESIST(0.25),
		RESIST(0.5),
		NEUTRAL(1.0),
		EFFECTIVE(2.0),
		QUAD_EFFECTIVE(4.0),
		;
		
		private double multiplier;
		
		private Efficacy(double multiplier)
		{
			this.multiplier = multiplier;
		}
		
		public double getMultiplier()
		{
			return multiplier;
		}
	}
	
	private void setUpOnDefense()
	{
		Type primaryType = typesToCheck.get(0);
		Optional<Type> secondaryType = Optional.ofNullable(typesToCheck.get(1));
		
		for(Type type: typesToCheckAgainst)
			for(Efficacy efficacy : Efficacy.values())
				for(int i = 0; i < efficacyMatrix.length - 1; i++)
				{
					double efficacyOperand1 = efficacyMatrix[i][primaryType.getId()];
					double efficacyOperand2 = secondaryType.isPresent() ? efficacyMatrix[i][secondaryType.get().getId()] : 1.0;
					
					if(efficacyOperand1 * efficacyOperand2 == efficacy.multiplier)
						interactions.get(efficacy).add(type);
				}
	}
	
	private void setUpOnOffense()
	{
		Set<Type> effective = new HashSet<>();
		Set<Type> neutral = new HashSet<>();
		Set<Type> resist = new HashSet<>();
		Set<Type> immune = new HashSet<>();
		Iterator<Type> iter = typesToCheck.iterator();
		
		immune.addAll(typesToCheckAgainst);
		
		while (iter.hasNext())
		{
			Type type = iter.next();
			effective.addAll(getTypesWithOffenseEffectiveness(type, Efficacy.EFFECTIVE));
			neutral.addAll(getTypesWithOffenseEffectiveness(type, Efficacy.NEUTRAL));
			resist.addAll(getTypesWithOffenseEffectiveness(type, Efficacy.RESIST));
		}
		
		//Remove duplicates with priority: effective > neutral > resist > immune
		neutral.removeAll(effective);
		resist.removeAll(effective);
		immune.removeAll(effective);
		
		resist.removeAll(neutral);
		immune.removeAll(neutral);
		
		immune.removeAll(resist);
		
		//Populate the wrapper
		for(Type type : effective)
			interactions.get(Efficacy.EFFECTIVE).add(type);
		for(Type type : neutral)
			interactions.get(Efficacy.NEUTRAL).add(type);
		for(Type type : resist)
			interactions.get(Efficacy.RESIST).add(type);
		for(Type type : immune)
			interactions.get(Efficacy.IMMUNE).add(type);
		
	}
	
	private Set<Type> getTypesWithOffenseEffectiveness(Type atk, Efficacy efficacy)
	{
		Set<Type> types = new HashSet<>(typesToCheckAgainst);
		Type type;
		
		Iterator<Type> iter = types.iterator();
		while(iter.hasNext())
		{
			type = iter.next();
			if(efficacyMatrix[atk.getId()][type.getId()] != efficacy.multiplier)
				iter.remove();
		}
			
		return types;
	}
}
