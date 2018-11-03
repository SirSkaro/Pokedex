package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum ColorTracker 
{
	;
	
	private static Map<String, Color> typeColorMap = new HashMap<>();
	private static Map<String, Color> versionColorMap = new HashMap<>();
	
	public static Color getColorForType(String type)
	{
		type = type.toLowerCase();
		return typeColorMap.get(type);
	}
	
	public static Color getColorForWrapper(TypeInteractionWrapper wrapper)
	{
		Color result = null;
		
		for(TypeData type : wrapper.getTypes())
			result = blend(type.toColor(), result);
		
		return result;
	}
	
	public static Color getColorForVersion(String ver)
	{
		ver = ver.toLowerCase();
		return versionColorMap.get(ver);
	}
	
	public static Color getColorForAbility()
	{
		return new Color(0x66E1FB);
	}
	
	public static Color getColorForPatreon()
	{
		return new Color(0xF96854);
	}
	
	private static Color blend(Color c0, Color c1) 
	{
		if(c1 == null)
			return c0;
		
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;
		
		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());
		
		return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	public static void initialize()
	{
		typeColorMap.put("normal".intern(), new Color(0xA8A77A));
		typeColorMap.put("fighting".intern(), new Color(0xC22E28));
		typeColorMap.put("flying".intern(), new Color(0xA98FF3));
		typeColorMap.put("poison".intern(), new Color(0xA33EA1));
		typeColorMap.put("ground".intern(), new Color(0xE2BF65));
		typeColorMap.put("rock".intern(), new Color(0xB6A136));
		typeColorMap.put("bug".intern(), new Color(0xA6B91A));
		typeColorMap.put("ghost".intern(), new Color(0x735797));
		typeColorMap.put("steel".intern(), new Color(0xB7B7CE));
		typeColorMap.put("fire".intern(), new Color(0xEE8130));
		typeColorMap.put("water".intern(), new Color(0x6390F0));
		typeColorMap.put("grass".intern(), new Color(0x7AC74C));
		typeColorMap.put("electric".intern(), new Color(0xF7D02C));
		typeColorMap.put("psychic".intern(), new Color(0xF95587));
		typeColorMap.put("ice".intern(), new Color(0x96D9D6));
		typeColorMap.put("dragon".intern(), new Color(0x6F35FC));
		typeColorMap.put("dark".intern(), new Color(0x705746));
		typeColorMap.put("fairy".intern(), new Color(0xD685AD));
		typeColorMap.put("bird".intern(), new Color(0xA4BBB3));
		
		versionColorMap.put("red".intern(), new Color(0xFF1111));
		versionColorMap.put("blue".intern(), new Color(0x1111FF));
		versionColorMap.put("yellow".intern(), new Color(0xFFD733));
		versionColorMap.put("gold".intern(), new Color(0xDAA520));
		versionColorMap.put("silver".intern(), new Color(0xC0C0C0));
		versionColorMap.put("crystal".intern(), new Color(0x4fD9FF));
		versionColorMap.put("ruby".intern(), new Color(0xA00000));
		versionColorMap.put("sapphire".intern(), new Color(0x0000A0));
		versionColorMap.put("emerald".intern(), new Color(0x00A000));
		versionColorMap.put("firered".intern(), new Color(0xFF7327));
		versionColorMap.put("leafgreen".intern(), new Color(0x00DD00));
		versionColorMap.put("diamond".intern(), new Color(0xAAAAFF));
		versionColorMap.put("pearl".intern(), new Color(0xFFAAAA));
		versionColorMap.put("platinum".intern(), new Color(0x999999));
		versionColorMap.put("heartgold".intern(), new Color(0xB69E00));
		versionColorMap.put("soulsilver".intern(), new Color(0xC0C0E1));
		versionColorMap.put("black".intern(), new Color(0x444444));
		versionColorMap.put("white".intern(), new Color(0xE1E1E1));
		versionColorMap.put("black2".intern(), new Color(0x444444));
		versionColorMap.put("white2".intern(), new Color(0xE1E1E1));
		versionColorMap.put("x".intern(), new Color(0x6376B8));
		versionColorMap.put("y".intern(), new Color(0xED5540));
		versionColorMap.put("omegaruby".intern(), new Color(0xCF3025));
		versionColorMap.put("alphasapphire".intern(), new Color(0x1768D1));
		versionColorMap.put("sun".intern(), new Color(0xF1912B));
		versionColorMap.put("moon".intern(), new Color(0x5599CA));
		versionColorMap.put("ultrasun".intern(), new Color(0xFAA71B));
		versionColorMap.put("ultramoon".intern(), new Color(0x179CD7));
	}
}
