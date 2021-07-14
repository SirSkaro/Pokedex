package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class MetaArgument extends CommandArgument 
{
	public MetaArgument()
	{
		super("Meta", null);
	}

	public void setUp(String argument, Language lang) 
	{
		this.dbForm = TextUtility.dbFormat(argument, lang);
		this.rawInput = argument;
		
		if(!Meta.isMeta(this.dbForm))
		{
			this.valid = false;
			return;
		}
		
		this.valid = true;
		this.flexForm = this.dbForm;
	}
	
	private enum Meta 
	{
		LC("lc"),
		NU("nu"),
		PU("pu"),
		RU("ru"),
		UU("uu"),
		OU("ou"),
		UBER("uber"),
		;
		
		private String name;
		
		private Meta(String name)
		{
			this.name = name;
		}
		
		public static boolean isMeta(String metaName)
		{
			for(Meta meta : Meta.values())
				if(meta.name.equals(metaName))
					return true;
			
			return false;
		}
	}
}
