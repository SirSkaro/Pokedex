package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;

public class TypeInteractionWrapper 
{
	private ArrayList<String> resistx4;
	private ArrayList<String> resistx2;
	private ArrayList<String> immune;
	private ArrayList<String> neutral;
	private ArrayList<String> effx2;
	private ArrayList<String> effx4;
	private String type1;
	private String type2;
	private String type3;
	private String type4;
	private Color colorBlend;
	
	//Constructor
	public TypeInteractionWrapper()
	{
		resistx4 = new ArrayList<String>();
		resistx2 = new ArrayList<String>();
		immune = new ArrayList<String>();
		effx2 = new ArrayList<String>();
		effx4 = new ArrayList<String>();
		type1 = null;
		type2 = null;
		type3 = null;
		type4 = null;
		colorBlend = null;
	}
	
	//Get and set methods
	public void setRx4(ArrayList<String> list){ resistx4 = list; }
	public void setRx2(ArrayList<String> list){ resistx2 = list; }
	public void setN(ArrayList<String> list){ neutral = list; }
	public void setImm(ArrayList<String> list){ immune = list; }
	public void setEx2(ArrayList<String> list){ effx2 = list; }
	public void setEx4(ArrayList<String> list){ effx4 = list; }
	public void setType1(String t){ type1 = t; }
	public void setType2(String t){ type2 = t; }
	public void setType3(String t){ type3 = t; }
	public void setType4(String t){ type4 = t; }
	public void setColor(Color c){ colorBlend = c; }
	public void setType(String t, int i)
	{
		switch(i)
		{
		case 1:	type1 = t;
			return;
		case 2:	type2 = t;
			return;
		case 3:	type3 = t;
			return;
		case 4:	type4 = t;
			return;
		default:
			return;
		}
	}
	
	public void setInteraction(double mult, ArrayList<String> list)
	{
		if(mult == 0.0)
			immune = list;
		else if(mult == 0.25)
			resistx4 = list;
		else if(mult == 0.5)
			resistx2 = list;
		else if(mult == 1.0)
			neutral = list;
		else if(mult == 2.0)
			effx2 = list;
		else if(mult == 4.0)
			effx4 = list;
	}
	
	public ArrayList<String> getRx4() { return resistx4; }
	public ArrayList<String> getRx2() { return resistx2; }
	public ArrayList<String> getN() { return neutral; }
	public ArrayList<String> getImm() { return immune; }
	public ArrayList<String> getEx2() { return effx2; }
	public ArrayList<String> getEx4() { return effx4; }
	public String getType1() { return type1; }
	public String getType2() { return type2; }
	public String getType3() { return type3; }
	public String getType4() { return type4; }
	public Color getColor() { return colorBlend; }
	
	public String listToString(double mult)
	{
		String output = "";
		
		if(mult == 0.0)
		{
			for(int i = 0; i < immune.size(); i++)
				output += immune.get(i) + ", ";
		}
		else if(mult == 0.25)
		{
			for(int i = 0; i < resistx4.size(); i++)
				output += resistx4.get(i) + ", ";
		}
		else if(mult == 0.5)
		{
			for(int i = 0; i < resistx2.size(); i++)
				output += resistx2.get(i) + ", ";
		}
		else if(mult == 1.0)
		{
			for(int i = 0; i < neutral.size(); i++)
				output += neutral.get(i) + ", ";
		}
		else if(mult == 2.0)
		{
			for(int i = 0; i < effx2.size(); i++)
				output += effx2.get(i) + ", ";
		}
		else if(mult == 4.0)
		{
			for(int i = 0; i < effx4.size(); i++)
				output += effx4.get(i) + ", ";
		}
		else
			return null;
		
		if(output.length() == 0)
			return output;
		
		return output.substring(0, output.length() - 2);
	}
	
	public String typesToString()
	{
		String output = "";
		
		output += (type1 == null) ? "" : type1 ;
		output += (type2 == null) ? "" : "/"+type2 ;
		output += (type3 == null) ? "" : "/"+type3 ;
		output += (type4 == null) ? "" : "/"+type4 ;
		
		return output;
	}
	
}
