package skaro.pokedex.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discord4j.rest.util.Color;
import skaro.pokedex.data_processor.TypeEfficacyWrapper;
import skaro.pokeflex.objects.type.Type;

public class ColorService implements PokedexService
{
	private final Map<String, Color> typeColorMap;
	private final Map<String, Color> versionColorMap;
	private final Map<String, Color> cardTypeColorMap;
	
	public ColorService()
	{
		typeColorMap = new HashMap<>();
		versionColorMap = new HashMap<>();
		cardTypeColorMap = new HashMap<>();
		initialize();
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.COLOR;
	}
	
	public Color getColorForType(String type)
	{
		type = type.toLowerCase();
		return typeColorMap.get(type);
	}
	
	public Color getPokedexColor()
	{
		return Color.of(0xD60B01);
	}
	
	public Color getCardColor() 
	{
		return Color.of(0xFFE068);
	}
	
	public Color getColorForWrapper(TypeEfficacyWrapper wrapper)
	{
		Color result = null;
		
		for(Type type : wrapper.getTypes())
			result = blend(typeColorMap.get(type.getName()), result);
		
		return result;
	}
	
	public Color getColorForVersion(String ver)
	{
		ver = ver.toLowerCase();
		return versionColorMap.get(ver);
	}
	
	public Color getColorForCardType(List<String> types)
	{
		if(types.size() == 1) 
			return cardTypeColorMap.get(types.get(0).toLowerCase());
		
		Color color1 = cardTypeColorMap.get(types.get(0).toLowerCase());
		Color color2 = cardTypeColorMap.get(types.get(1).toLowerCase());
		return this.blend(color1, color2);
	}
	
	public Color getColorForAbility()
	{
		return Color.of(0x66E1FB);
	}
	
	public Color getColorForPatreon()
	{
		return Color.of(0xF96854);
	}
	
	public Color getColorForItem()
	{
		return Color.of(0xE89800);
	}
	
	private Color blend(Color c1, Color c2) 
	{
		if(c2 == null)
			return c1;
		
		java.awt.Color color1 = new java.awt.Color(c1.getRGB());
		java.awt.Color color2 = new java.awt.Color(c2.getRGB());
		
		double totalAlpha = color1.getAlpha() + color2.getAlpha();
		double weight0 = color1.getAlpha() / totalAlpha;
		double weight1 = color2.getAlpha() / totalAlpha;
		
		double r = weight0 * color1.getRed() + weight1 * c2.getRed();
		double g = weight0 * color1.getGreen() + weight1 * c2.getGreen();
		double b = weight0 * color1.getBlue() + weight1 * c2.getBlue();
		
		return Color.of((int)r, (int)g, (int)b);
	}
	
	private void initialize()
	{
		typeColorMap.put("normal".intern(), Color.of(0xA8A77A));
		typeColorMap.put("fighting".intern(), Color.of(0xC22E28));
		typeColorMap.put("flying".intern(), Color.of(0xA98FF3));
		typeColorMap.put("poison".intern(), Color.of(0xA33EA1));
		typeColorMap.put("ground".intern(), Color.of(0xE2BF65));
		typeColorMap.put("rock".intern(), Color.of(0xB6A136));
		typeColorMap.put("bug".intern(), Color.of(0xA6B91A));
		typeColorMap.put("ghost".intern(), Color.of(0x735797));
		typeColorMap.put("steel".intern(), Color.of(0xB7B7CE));
		typeColorMap.put("fire".intern(), Color.of(0xEE8130));
		typeColorMap.put("water".intern(), Color.of(0x6390F0));
		typeColorMap.put("grass".intern(), Color.of(0x7AC74C));
		typeColorMap.put("electric".intern(), Color.of(0xF7D02C));
		typeColorMap.put("psychic".intern(), Color.of(0xF95587));
		typeColorMap.put("ice".intern(), Color.of(0x96D9D6));
		typeColorMap.put("dragon".intern(), Color.of(0x6F35FC));
		typeColorMap.put("dark".intern(), Color.of(0x705746));
		typeColorMap.put("fairy".intern(), Color.of(0xD685AD));
		typeColorMap.put("bird".intern(), Color.of(0xA4BBB3));
		
		versionColorMap.put("red".intern(), Color.of(0xFF1111));
		versionColorMap.put("blue".intern(), Color.of(0x1111FF));
		versionColorMap.put("yellow".intern(), Color.of(0xFFD733));
		versionColorMap.put("gold".intern(), Color.of(0xDAA520));
		versionColorMap.put("silver".intern(), Color.of(0xC0C0C0));
		versionColorMap.put("crystal".intern(), Color.of(0x4fD9FF));
		versionColorMap.put("ruby".intern(), Color.of(0xA00000));
		versionColorMap.put("sapphire".intern(), Color.of(0x0000A0));
		versionColorMap.put("emerald".intern(), Color.of(0x00A000));
		versionColorMap.put("firered".intern(), Color.of(0xFF7327));
		versionColorMap.put("leafgreen".intern(), Color.of(0x00DD00));
		versionColorMap.put("diamond".intern(), Color.of(0xAAAAFF));
		versionColorMap.put("pearl".intern(), Color.of(0xFFAAAA));
		versionColorMap.put("platinum".intern(), Color.of(0x999999));
		versionColorMap.put("heartgold".intern(), Color.of(0xB69E00));
		versionColorMap.put("soulsilver".intern(), Color.of(0xC0C0E1));
		versionColorMap.put("black".intern(), Color.of(0x444444));
		versionColorMap.put("white".intern(), Color.of(0xE1E1E1));
		versionColorMap.put("black2".intern(), Color.of(0x444444));
		versionColorMap.put("white2".intern(), Color.of(0xE1E1E1));
		versionColorMap.put("x".intern(), Color.of(0x6376B8));
		versionColorMap.put("y".intern(), Color.of(0xED5540));
		versionColorMap.put("omegaruby".intern(), Color.of(0xCF3025));
		versionColorMap.put("alphasapphire".intern(), Color.of(0x1768D1));
		versionColorMap.put("sun".intern(), Color.of(0xF1912B));
		versionColorMap.put("moon".intern(), Color.of(0x5599CA));
		versionColorMap.put("ultrasun".intern(), Color.of(0xFAA71B));
		versionColorMap.put("ultramoon".intern(), Color.of(0x179CD7));
		
		cardTypeColorMap.put("grass", Color.of(0x7db808));
		cardTypeColorMap.put("fire", Color.of(0xe24242));
		cardTypeColorMap.put("water", Color.of(0x5bc7e5));
		cardTypeColorMap.put("lightning", Color.of(0xfab536));
		cardTypeColorMap.put("fighting", Color.of(0xff501f));
		cardTypeColorMap.put("psychic", Color.of(0xa65e9a));
		cardTypeColorMap.put("colorless", Color.of(0xe5d6d0));
		cardTypeColorMap.put("darkness", Color.of(0xe5d6d0));
		cardTypeColorMap.put("metal", Color.of(0x8a776e));
		cardTypeColorMap.put("dragon", Color.of(0xc6a114));
		cardTypeColorMap.put("fairy", Color.of(0xe03a83));
	}
}
