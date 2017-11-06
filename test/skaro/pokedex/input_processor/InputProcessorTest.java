package skaro.pokedex.input_processor;

import junit.framework.TestCase;
import skaro.pokedex.core.CommandLibrary;
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
import skaro.pokedex.database_resources.DatabaseService;

public class InputProcessorTest extends TestCase {
	InputProcessor ip;

	protected void setUp() throws Exception {
		super.setUp();
		ip = new InputProcessor(getLib(), new DatabaseService() {
			
		});
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
	
	public void testProcessInput() {
		String command = "randpoke()";
		Input result = this.ip.processInput(command);
		System.out.println(result);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
