package skaro.pokedex.data_processor;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

public class TTSConverter 
{
	private MaryInterface englishTTS;
    
    public TTSConverter()
    {	
    	System.out.println("[TTSConverter] Initializing English MaryTTS server...");
        try
        {
    		englishTTS = new LocalMaryInterface();
    		englishTTS.setVoice("cmu-slt-hsmm");
        	
        	System.out.println("[TTSConverter] English MaryTTS server successfully initialized");
        }
        catch (MaryConfigurationException ex)
        {
        	System.out.println("[TTSConverter] Could not initialize MaryTTS servers. Exiting...");
            System.exit(1);
        }
    }
    
    public AudioInputStream convertToAudio(String input)
    {
        try
        {
        	//Make sure only one thread can use the MaryInterface at one time
        	synchronized(englishTTS)
        	{
            	return englishTTS.generateAudio(input);
        	}
        }
        catch (SynthesisException ex)
        {
            System.out.println("[TTSConverter] Error saying phrase.");
        }
        
        return null;
    }
}
