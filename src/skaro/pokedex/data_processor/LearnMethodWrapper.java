package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.move_learn_method.MoveLearnMethod;
import skaro.pokeflex.objects.pokemon.VersionGroupDetail;

public class LearnMethodWrapper 
{
	private Move move;
	private String specifiedMove;
	private boolean recognized;
	private List<MoveLearnMethod> methods;
	
	public LearnMethodWrapper(Map<String, skaro.pokeflex.objects.pokemon.Move> learnableMoves, Move move)
	{
		this.recognized = true;
		this.move = move;
		this.methods = new ArrayList<>(4);
		
		skaro.pokeflex.objects.pokemon.Move learnableMove = learnableMoves.get(move.getName());
		if(learnableMove != null)
			for(VersionGroupDetail details : learnableMove.getVersionGroupDetails())
			{
				MoveLearnMethod method = LearnMethodData.getByName(details.getMoveLearnMethod().getName());
				methods.add(method);
			}
	}
	
	public LearnMethodWrapper(Move move)
	{
		this.recognized = true;
		this.move = move;
		this.methods = new ArrayList<>(4);
	}
	
	public LearnMethodWrapper(String specifiedMove)
	{
		this.specifiedMove = specifiedMove;
		this.recognized = false;
		this.methods = new ArrayList<>(0);
	}
	
	public Move getMove() { return this.move; }
	public String getSpecifiedMove() { return this.specifiedMove; }
	public boolean isRecognized() { return this.recognized; }
	public List<MoveLearnMethod> getMethods() { return this.methods; }
	public boolean moveIsLearnable() { return !this.methods.isEmpty(); }
	
	public void populateMethods(Map<String, skaro.pokeflex.objects.pokemon.Move> learnableMoves)
	{
		skaro.pokeflex.objects.pokemon.Move learnableMove = learnableMoves.get(move.getName());
		if(learnableMove != null)
			for(VersionGroupDetail details : learnableMove.getVersionGroupDetails())
			{
				MoveLearnMethod method = LearnMethodData.getByName(details.getMoveLearnMethod().getName());
				methods.add(method);
			}
	}
}
