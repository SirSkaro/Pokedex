package skaro.pokedex.communicator.publish_recipients;

import skaro.pokedex.services.ServiceConsumerException;

public abstract class PublicationRecipient {
	protected RecipientConfig config;
	
	public PublicationRecipient(RecipientConfig config) throws ServiceConsumerException {
		this.config = config;
	}
	
	public abstract boolean sendPublication(int connectedGuilds, long botId);
		
	public RecipientConfig getConfig() {
		return config;
	}
	
}
