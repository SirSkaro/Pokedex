package skaro.pokedex.services;

import skaro.pokedex.communicator.publish_recipients.RecipientConfig;
import skaro.pokedex.communicator.publish_recipients.Recipients;

public interface ConfigurationService extends PokedexService {

	Integer getShardIndex();
	Integer getShardTotal();
	String getVersion();
	String getDiscordAuthToken();
	RecipientConfig getPublishRecipientConfig(Recipients recipient);
	String getModelBasePath();
	String getHelpGifPath();
	String getZMoveClipPath();
	String getPokeFlexURL();
	String getPatreonAuthToken();
	String getTcgApiKey();
}
