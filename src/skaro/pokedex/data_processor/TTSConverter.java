package skaro.pokedex.data_processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import skaro.pokedex.input_processor.Language;

public class TTSConverter 
{
	private MaryInterface maryTTS;
	private Map<Language, String> voiceMap;
    
    public TTSConverter()
    {	
    	System.out.println("[TTSConverter] Initializing English MaryTTS server...");
        try
        {
    		maryTTS = new LocalMaryInterface();
    		voiceMap = new HashMap<Language,String>();
    		
    		voiceMap.put(Language.ENGLISH, "dfki-spike-hsmm");
    		voiceMap.put(Language.FRENCH, "enst-dennys-hsmm");
    		voiceMap.put(Language.GERMAN, "dfki-pavoque-neutral-hsmm");
    		voiceMap.put(Language.ITALIAN, "istc-lucia-hsmm");
        	
        	System.out.println("[TTSConverter] MaryTTS server successfully initialized");
        }
        catch (MaryConfigurationException ex)
        {
        	System.out.println("[TTSConverter] Could not initialize MaryTTS servers. Exiting...");
            System.exit(1);
        }
    }
    
    public Optional<AudioInputStream> convertToAudio(Language lang, String input)
    {
    	if(!voiceMap.containsKey(lang))
    		return Optional.empty();
    	
        try
        {
        	//Make sure only one thread can use the MaryInterface at one time
        	synchronized(maryTTS)
        	{
        		maryTTS.setVoice(voiceMap.get(lang));
            	return Optional.of(maryTTS.generateAudio(input));
        	}
        }
        catch (SynthesisException ex)
        {
            System.out.println("[TTSConverter] Error saying phrase.");
            return Optional.empty();
        }
    }
}
