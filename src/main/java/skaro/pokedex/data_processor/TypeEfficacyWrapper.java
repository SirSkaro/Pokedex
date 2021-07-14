package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.type.DamageRelations;
import skaro.pokeflex.objects.type.Type;

public class TypeEfficacyWrapper implements IFlexObject
{
	private Map<Efficacy, List<Type>> interactions;
	private List<Type> typesToCheck;
	private List<Type> typesToCheckAgainst;
	
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
	
	public static class EfficacyInteractionBuilder
	{
		private List<Type> typesToCheck;
		private List<Type> typesToCheckAgainst;
		private EfficacyCategory efficacyCategory;
		
		private EfficacyInteractionBuilder() 
		{
			typesToCheck = new ArrayList<>();
		}
		
		public static EfficacyInteractionBuilder newInstance()
		{
			EfficacyInteractionBuilder builder = new EfficacyInteractionBuilder();
			return builder;
		}
		
		public EfficacyInteractionBuilder withEfficacyCategory(EfficacyCategory category)
		{
			efficacyCategory = category;
			return this;
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
			
			if(efficacyCategory == EfficacyCategory.OFFENSE)
				result.setUpOnOffense();
			else
				result.setUpOnDefense();
			
			return result;
		}
	}
	
	public enum EfficacyCategory
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
		for(Type typeToCheckAgainst : typesToCheckAgainst)
		{
			Efficacy efficacy = checkEfficacyOnDefense(typeToCheckAgainst);
			interactions.get(efficacy).add(typeToCheckAgainst);
		}
	}
	
	private Efficacy checkEfficacyOnDefense(Type type)
	{
		DamageRelations primaryTypeRelations = typesToCheck.get(0).getDamageRelations();
		DamageRelations secondaryTypeRelations = null;
		
		if(typesToCheck.size() > 1)
			secondaryTypeRelations = typesToCheck.get(1).getDamageRelations();
		
		if(secondaryTypeRelations != null)
			return checkEfficacyOnDefense(type, primaryTypeRelations, secondaryTypeRelations);
		return checkEfficacyOnDefense(type, primaryTypeRelations);
	}
	
	private Efficacy checkEfficacyOnDefense(Type type, DamageRelations primaryTypeRelations, DamageRelations secondaryTypeRelations)
	{
		Efficacy efficacyOfPrimaryType = checkEfficacyOnDefense(type, primaryTypeRelations);
		Efficacy efficacyOfSecondaryType = checkEfficacyOnDefense(type, secondaryTypeRelations);
		
		if(efficacyOfPrimaryType == Efficacy.IMMUNE || efficacyOfSecondaryType == Efficacy.IMMUNE)
			return Efficacy.IMMUNE;
		else if(efficacyOfPrimaryType == Efficacy.EFFECTIVE && efficacyOfSecondaryType == Efficacy.EFFECTIVE)
			return Efficacy.QUAD_EFFECTIVE;
		else if(efficacyOfPrimaryType == Efficacy.NEUTRAL && efficacyOfSecondaryType == Efficacy.EFFECTIVE)
			return Efficacy.EFFECTIVE;
		else if(efficacyOfPrimaryType == Efficacy.EFFECTIVE && efficacyOfSecondaryType == Efficacy.NEUTRAL)
			return Efficacy.EFFECTIVE;
		else if(efficacyOfPrimaryType == Efficacy.NEUTRAL && efficacyOfSecondaryType == Efficacy.NEUTRAL)
			return Efficacy.NEUTRAL;
		else if(efficacyOfPrimaryType == Efficacy.EFFECTIVE && efficacyOfSecondaryType == Efficacy.RESIST)
			return Efficacy.NEUTRAL;
		else if(efficacyOfPrimaryType == Efficacy.RESIST && efficacyOfSecondaryType == Efficacy.EFFECTIVE)
			return Efficacy.NEUTRAL;
		else if(efficacyOfPrimaryType == Efficacy.RESIST && efficacyOfSecondaryType == Efficacy.NEUTRAL)
			return Efficacy.RESIST;
		else if(efficacyOfPrimaryType == Efficacy.NEUTRAL && efficacyOfSecondaryType == Efficacy.RESIST)
			return Efficacy.RESIST;
			
		return Efficacy.QUAD_RESIST;
	}
	
	private Efficacy checkEfficacyOnDefense(Type type, DamageRelations typeRelations)
	{
		if(typeRelations.takesDoubleDamageFrom(type))
			return Efficacy.EFFECTIVE;
		else if(typeRelations.takesHalfDamageFrom(type))
			return Efficacy.RESIST;
		else if(typeRelations.takesNoDamageFrom(type))
			return Efficacy.IMMUNE;
		
		return Efficacy.NEUTRAL;
	}
	
	private void setUpOnOffense()
	{
		Set<Type> effective = new HashSet<>();
		Set<Type> neutral = new HashSet<>();
		Set<Type> resist = new HashSet<>();
		Set<Type> immune = new HashSet<>();
		
		for(Type typeToCheck : typesToCheck)
		{
			DamageRelations damageRelations = typeToCheck.getDamageRelations();
			for(Type typeToCheckAgainst : typesToCheckAgainst)
			{
				if(damageRelations.causesDoubleDamageTo(typeToCheckAgainst))
					effective.add(typeToCheckAgainst);
				else if(damageRelations.causesHalfDamageTo(typeToCheckAgainst))
					resist.add(typeToCheckAgainst);
				else if(damageRelations.causesNoDamageTo(typeToCheckAgainst))
					immune.add(typeToCheckAgainst);
				else
					neutral.add(typeToCheckAgainst);
			}
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
	
}
