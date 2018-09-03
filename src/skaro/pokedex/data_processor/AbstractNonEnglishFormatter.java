package skaro.pokedex.data_processor;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.api.PokeFlexFactory;

public abstract class AbstractNonEnglishFormatter 
{
	protected Map<Language, String> fieldMap;
	protected PokeFlexFactory factory;
	protected PerkChecker checker;
	
	public AbstractNonEnglishFormatter(PokeFlexFactory pff, PerkChecker pc)
	{
		fieldMap = new HashMap<Language, String>();
		factory = pff;
		checker = pc;
	}
	
	public abstract Response getNonEnglishReply(Input input, Language lang);
}
