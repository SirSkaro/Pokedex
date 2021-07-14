package skaro.pokeflex.api;

import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.berry.Berry;
import skaro.pokeflex.objects.berry_firmness.BerryFirmness;
import skaro.pokeflex.objects.berry_flavor.BerryFlavor;
import skaro.pokeflex.objects.card.Card;
import skaro.pokeflex.objects.card.Cards;
import skaro.pokeflex.objects.contest_effect.ContestEffect;
import skaro.pokeflex.objects.contest_type.ContestType;
import skaro.pokeflex.objects.egg_group.EggGroup;
import skaro.pokeflex.objects.encounter.Encounter;
import skaro.pokeflex.objects.encounter_condition.EncounterCondition;
import skaro.pokeflex.objects.encounter_condition_value.EncounterConditionValue;
import skaro.pokeflex.objects.encounter_method.EncounterMethod;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_trigger.EvolutionTrigger;
import skaro.pokeflex.objects.growth_rate.GrowthRate;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.item_attribute.ItemAttribute;
import skaro.pokeflex.objects.item_category.ItemCategory;
import skaro.pokeflex.objects.item_fling_effect.ItemFlingEffect;
import skaro.pokeflex.objects.item_pocket.ItemPocket;
import skaro.pokeflex.objects.location.Location;
import skaro.pokeflex.objects.location_area.LocationArea;
import skaro.pokeflex.objects.machine.Machine;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.move_battle_style.MoveBattleStyle;
import skaro.pokeflex.objects.move_category.MoveCategory;
import skaro.pokeflex.objects.move_damage_class.MoveDamageClass;
import skaro.pokeflex.objects.move_learn_method.MoveLearnMethod;
import skaro.pokeflex.objects.move_target.MoveTarget;
import skaro.pokeflex.objects.nature.Nature;
import skaro.pokeflex.objects.pokedex.Pokedex;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_color.PokemonColor;
import skaro.pokeflex.objects.pokemon_form.PokemonForm;
import skaro.pokeflex.objects.pokemon_habitat.PokemonHabitat;
import skaro.pokeflex.objects.pokemon_shape.PokemonShape;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.region.Region;
import skaro.pokeflex.objects.set.Set;
import skaro.pokeflex.objects.stat.Stat;
import skaro.pokeflex.objects.super_contest_effect.SuperContestEffect;
import skaro.pokeflex.objects.type.Type;
import skaro.pokeflex.objects.version.Version;

public enum Endpoint 
{
	ABILITY(Ability.class, "ability"),
	BERRY(Berry.class, "berry"),
	BERRY_FIRMNESS(BerryFirmness.class, "berry-firmness"),
	BERRY_FLAVOR(BerryFlavor.class, "berry-flavor"),
	CARD(Card.class, "cards"),
	CARDS(Cards.class, "cards"),
	CONTEST_EFFECT(ContestEffect.class, "contest-effect"),
	CONTEST_TYPE(ContestType.class, "contest-type"),
	EGG_GROUP(EggGroup.class, "egg-group"),
	ENCOUNTER(Encounter.class, "pokemon"),	//the last param should be "encounters"
	ENCOUNTER_CONDITION(EncounterCondition.class, "encounter-condition"),
	ENCOUNTER_CONDITION_VALUE(EncounterConditionValue.class, "encounter-condition-value"),
	ENCOUNTER_METHOD(EncounterMethod.class, "encounter-method"),
	EVOLUTION_CHAIN(EvolutionChain.class, "evolution-chain"),
	EVOLUTION_TRIGGER(EvolutionTrigger.class, "evolution-trigger"),
	GROWTH_RATE(GrowthRate.class, "growth-rate"),
	ITEM(Item.class, "item"),
	ITEM_ATTRIBUTE(ItemAttribute.class, "item-attribute"),
	ITEM_CATEGORY(ItemCategory.class, "item-category"),
	ITEM_FLING_EFFECT(ItemFlingEffect.class, "item-fling-effect"),
	ITEM_POCKET(ItemPocket.class, "item-pocket"),
	LOCATION(Location.class, "location"),
	LOCATION_AREA(LocationArea.class, "location-area"),
	MACHINE(Machine.class, "machine"),
	MOVE(Move.class, "move"),
	MOVE_BATTLE_STYLE(MoveBattleStyle.class, "move-battle-style"),
	MOVE_CATEGORY(MoveCategory.class, "move-category"),
	MOVE_DAMAGE_CLASS(MoveDamageClass.class, "move-damage-class"),
	MOVE_LEARN_METHOD(MoveLearnMethod.class, "move-learn-method"),
	MOVE_TARGET(MoveTarget.class, "move-target"),
	NATURE(Nature.class, "nature"),
	POKEDEX(Pokedex.class, "pokedex"),
	POKEMON(Pokemon.class, "pokemon"),
	POKEMON_COLOR(PokemonColor.class, "pokemon-color"),
	POKEMON_FORM(PokemonForm.class, "pokemon-form"),
	POKEMON_HABITAT(PokemonHabitat.class, "pokemon-habitat"),
	POKEMON_SHAPE(PokemonShape.class, "pokemon-shape"),
	POKEMON_SPECIES(PokemonSpecies.class, "pokemon-species"),
	REGION(Region.class, "region"),
	SET(Set.class, "set"),
	STAT(Stat.class, "stat"),
	SUPER_CONTEST_EFFECT(SuperContestEffect.class, "super-contest-effect"),
	TYPE(Type.class, "type"),
	VERSION(Version.class, "version"),
	;
	
	private String endpoint;
	private Class<?> wrapper;
	
	Endpoint(Class<?> wrap, String ep) throws ExceptionInInitializerError
	{
		if(!IFlexObject.class.isAssignableFrom(wrap))
			throw new ExceptionInInitializerError();
		
		wrapper = wrap;
		endpoint = ep;
	}
	
	public Class<?> getWrapperClass() { return wrapper; }
	public String getEnpoint() { return endpoint; }
}
