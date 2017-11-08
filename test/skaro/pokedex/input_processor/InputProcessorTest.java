package skaro.pokedex.input_processor;

import junit.framework.TestCase;
import skaro.pokedex.core.CommandLibrary;
import skaro.pokedex.data_processor.ICommand.ArgumentCategory;
import skaro.pokedex.data_processor.commands.AbilityCommand;
import skaro.pokedex.data_processor.commands.AboutCommand;
import skaro.pokedex.data_processor.commands.CommandsCommand;
import skaro.pokedex.data_processor.commands.CoverageCommand;
import skaro.pokedex.data_processor.commands.DataCommand;
import skaro.pokedex.data_processor.commands.DexCommand;
import skaro.pokedex.data_processor.commands.DonateCommand;
import skaro.pokedex.data_processor.commands.HelpCommand;
import skaro.pokedex.data_processor.commands.InviteCommand;
import skaro.pokedex.data_processor.commands.ItemCommand;
import skaro.pokedex.data_processor.commands.LearnCommand;
import skaro.pokedex.data_processor.commands.LocationCommand;
import skaro.pokedex.data_processor.commands.MoveCommand;
import skaro.pokedex.data_processor.commands.RandpokeCommand;
import skaro.pokedex.data_processor.commands.SetCommand;
import skaro.pokedex.data_processor.commands.StatsCommand;
import skaro.pokedex.data_processor.commands.WeakCommand;
import skaro.pokedex.database_resources.ComplexAbility;
import skaro.pokedex.database_resources.ComplexItem;
import skaro.pokedex.database_resources.ComplexMove;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.database_resources.LocationGroup;
import skaro.pokedex.database_resources.PokedexEntry;
import skaro.pokedex.database_resources.PokemonDataAccessor;
import skaro.pokedex.database_resources.SetGroup;
import skaro.pokedex.database_resources.SimpleAbility;
import skaro.pokedex.database_resources.SimpleMove;
import skaro.pokedex.database_resources.SimplePokemon;

public class InputProcessorTest extends TestCase {
	InputProcessor ip;


	protected void setUp() throws Exception {
		super.setUp();
		ip = new InputProcessor(getLib(), new MockPokedexAccessor()); 
	}
	

	public void testProcessInput() {
		String command = "randpoke()";
		Input result = this.ip.processInput(command);
		System.out.println(result);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static CommandLibrary getLib() {
		CommandLibrary lib = new CommandLibrary();
		lib.addToLibrary(RandpokeCommand.getInstance());
		lib.addToLibrary(StatsCommand.getInstance());
		lib.addToLibrary(DataCommand.getInstance());
		lib.addToLibrary(AbilityCommand.getInstance());
		lib.addToLibrary(ItemCommand.getInstance());
		lib.addToLibrary(MoveCommand.getInstance());
		lib.addToLibrary(LearnCommand.getInstance());
		lib.addToLibrary(WeakCommand.getInstance());
		lib.addToLibrary(CoverageCommand.getInstance());
		lib.addToLibrary(DexCommand.getInstance());
		lib.addToLibrary(SetCommand.getInstance());
		lib.addToLibrary(LocationCommand.getInstance());
		lib.addToLibrary(AboutCommand.getInstance());
		lib.addToLibrary(HelpCommand.getInstance());
		lib.addToLibrary(DonateCommand.getInstance());
		lib.addToLibrary(InviteCommand.getInstance());
		lib.addToLibrary(CommandsCommand.getInstance(lib.getLibrary()));
		return lib;	
	}
	
	class MockPokedexAccessor implements PokemonDataAccessor {

		@Override
		public boolean inMoveSet(String move, String pokemonId) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean resourceExists(ArgumentCategory ac, String resource) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isPokemon(String maybePoke) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isItem(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAbility(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isMove(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isVersion(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isMeta(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isType(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRegion(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isGen(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ComplexPokemon getComplexPokemon(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ComplexPokemon getRandomComplexPokemon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimplePokemon getSimplePokemon(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimplePokemon getRandomSimplePokemon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimpleAbility getSimpleAbility(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ComplexAbility getComplexAbility(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ComplexItem getComplexItem(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ComplexMove getComplexMove(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimpleMove getSimpleMove(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PokedexEntry getDexEntry(String pokemonId, String versionId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SetGroup getSetsForPokemon(String pokemonId, String tierId, int genId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocationGroup getLocation(String pokemon, String version) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String formatForDatabase(String s) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
