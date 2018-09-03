package skaro.pokedex.data_processor.formatters;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractNonEnglishFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.api.PokeFlexFactory;

public class DataFormatter extends AbstractNonEnglishFormatter 
{
	public DataFormatter(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
	}
	
	@Override
	public Response getNonEnglishReply(Input input, Language lang) 
	{
		return null;
	}

}
