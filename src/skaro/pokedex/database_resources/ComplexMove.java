package skaro.pokedex.database_resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A ComplexMove object represents all data for a Move. 
 * @author Ben
 *
 */
public class ComplexMove 
{
	private String name;
	private int power;
	private int zPower;
	private String zEffect;
	private String zBoost;
	private String crystal;
	private int accuracy;
	private String contest;
	private String category;
	private String target;
	private String type;
	private int basePP;
	private int maxPP;
	private String techDesc;
	private String shortDesc;
	private int priority;
	private ArrayList<String> flags;
	
	public static HashMap<String, String> flagDesc = new HashMap<String,String>();
	public static HashMap<String, String> effectDesc = new HashMap<String,String>();
	
	public ComplexMove(ResultSet basicData, ResultSet flagData)
	{
		String temp;
		try 
		{
			basicData.next();
			name = basicData.getString(2).intern();
			power = basicData.getInt(3);
			zPower = basicData.getInt(4);
			temp = basicData.getString(5);
				if(temp != null)
				zEffect = temp.intern();
			temp = basicData.getString(6);
				if(temp != null)
				zBoost = temp.intern();
			temp = basicData.getString(7);
				if(temp != null)
				crystal = temp.intern();
			accuracy = basicData.getInt(8);
			contest = basicData.getString(9).intern();
			category = basicData.getString(10).intern();
			target = basicData.getString(11).intern();
			type = basicData.getString(12).intern();
			basePP = basicData.getInt(13);
			maxPP = basicData.getInt(14);
			techDesc = basicData.getString(15);
			shortDesc = basicData.getString(16);
			priority = basicData.getInt(17);
			
			flags = new ArrayList<String>();
			while(flagData.next())
				flags.add(flagDesc.get(flagData.getString(1)));
		} 
		catch (SQLException e) 
		{
			name = null;
			System.err.println("[ComplexMove] SQL exception");
			e.printStackTrace();
		}
	}

	public String getName() { return name; }
	public int getPower() { return power; }
	public int getZPower() { return zPower; }
	public String getZEffect() { return effectDesc.get(zEffect); }
	public String getZBoost() { return zBoost; }
	public String getCrystal() { return crystal; }
	public int getAccuracy() { return accuracy; }
	public String getContest() { return contest; }
	public String getCategory() { return category; }
	public String getTarget() { return target; }
	public String getType() { return type; }
	public int getBasePP() { return basePP; }
	public int getMaxPP() { return maxPP; }
	public String getTechDesc() { return techDesc; }
	public String getShortDesc() { return shortDesc; }
	public int getPriority() { return priority; }
	
	public String getFlags()
	{
		StringBuilder flagList = new StringBuilder();
		for(String flag : flags)
			flagList.append(flag+" ");
		
		if(flagList.length() < 1)
			return null;
		return flagList.toString();
	}
	
	static
	{
		flagDesc.put("authentic", "Ignores a target's substitute.");
		flagDesc.put("bite", "Power is multiplied by 1.5 when used by a Pokemon with the Ability Strong Jaw.");
		flagDesc.put("bullet", "Has no effect on Pokemon with the Ability Bulletproof.");
		flagDesc.put("charge", "The user is unable to make a move between turns.");
		flagDesc.put("contact", "Makes contact.");
		flagDesc.put("defrost", "Thaws the user if executed successfully while the user is frozen.");
		flagDesc.put("distance", "Can target a Pokemon positioned anywhere in a Triple Battle.");
		flagDesc.put("gravity", "Prevented from being executed or selected during Gravity's effect.");
		flagDesc.put("heal", "Prevented from being executed or selected during Heal Block's effect.");
		flagDesc.put("mirror", "Can be copied by Mirror Move.");
		flagDesc.put("mystery", "Unknown effect.");
		flagDesc.put("nonsky", "Prevented from being executed or selected in a Sky Battle.");
		flagDesc.put("powder", "Has no effect on Grass-type Pokemon, Pokemon with the Ability Overcoat, and Pokemon holding Safety Goggles.");
		flagDesc.put("protect", "Blocked by Detect, Protect, Spiky Shield, and if not a Status move, King's Shield.");
		flagDesc.put("pulse", "Power is multiplied by 1.5 when used by a Pokemon with the Ability Mega Launcher.");
		flagDesc.put("punch", "Power is multiplied by 1.2 when used by a Pokemon with the Ability Iron Fist.");
		flagDesc.put("recharge", "If this move is successful, the user must recharge on the following turn and cannot make a move.");
		flagDesc.put("reflectable", "Bounced back to the original user by Magic Coat or the Ability Magic Bounce.");
		flagDesc.put("snatch", "Can be stolen from the original user and instead used by another Pokemon using Snatch.");
		flagDesc.put("sound", "Has no effect on Pokemon with the Ability Soundproof.");
		
		effectDesc.put("clearnegativeboost", "Clears negative stat boosts.");
		effectDesc.put("heal", "Heals the user.");
		effectDesc.put("healreplacement", "Heals the replacing Pokemon.");
		effectDesc.put("crit1", "Increases crit rate.");
		effectDesc.put("redirect", "Redirects all attacks this turn to the user.");
		effectDesc.put("curse", "If user is Ghost type, HP is restored. Otherwise, increases Attack by one more stage.");
	}
}
