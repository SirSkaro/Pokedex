package skaro.pokedex.services;

import java.util.stream.IntStream;

import skaro.pokedex.communicator.publish_recipients.RecipientConfig;
import skaro.pokedex.communicator.publish_recipients.Recipients;
import skaro.pokedex.core.PokedexEnvironment;
import skaro.pokedex.input_processor.DatabaseConfiguration;

public class EnvironmentConfigurationService implements ConfigurationService {

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFIG;
	}

	@Override
	public int[] getShardIndexes() {
		int startIndex = Integer.parseInt(System.getenv(PokedexEnvironment.POKEDEX_SHARD_INDEX_START.name()));
		int endIndex = Integer.parseInt(System.getenv(PokedexEnvironment.POKEDEX_SHARD_INDEX_END.name()));
		
		return IntStream.rangeClosed(startIndex, endIndex)
				.toArray();
	}

	@Override
	public Integer getShardTotal() {
		return Integer.parseInt(System.getenv(PokedexEnvironment.POKEDEX_SHARD_TOTAL.name()));
	}
	
	@Override
	public String getVersion() {
		return System.getenv(PokedexEnvironment.POKEDEX_VERSION.name());
	}

	@Override
	public String getDiscordAuthToken() {
		return System.getenv(PokedexEnvironment.POKEDEX_DISCORD_AUTH_TOKEN.name());
	}

	@Override
	public RecipientConfig getPublishRecipientConfig(Recipients recipient) {
		RecipientConfig config = new RecipientConfig();
		config.designatedShards = getShardIndexes();
		switch(recipient) {
		case BOTS_DISCORD:
			config.token = System.getenv(PokedexEnvironment.POKEDEX_PUBLISH_BOTS_DISCORD_AUTH_TOKEN.name());
			break;
		case CARBONITEX:
			config.token = System.getenv(PokedexEnvironment.POKEDEX_PUBLISH_CARBONITEX_AUTH_TOKEN.name());
			break;
		case DISCORD_BOTS:
			config.token = System.getenv(PokedexEnvironment.POKEDEX_PUBLISH_DISCORD_BOTS_AUTH_TOKEN.name());
			break;
		default:
			throw new IllegalArgumentException("Recipient not recognized");
		}
		
		return config;
	}

	public static DatabaseConfiguration getDBCredentials() {
		DatabaseConfiguration credentials = new DatabaseConfiguration();
		credentials.database = System.getenv(PokedexEnvironment.POKEDEX_DATABASE.name());
		credentials.username = System.getenv(PokedexEnvironment.POKEDEX_DATABASE_USER.name());
		credentials.password = System.getenv(PokedexEnvironment.POKEDEX_DATABASE_PASSWORD.name());
		credentials.url = System.getenv(PokedexEnvironment.POKEDEX_DATABASE_URL.name());
		
		return credentials;
	}

	@Override
	public String getModelBasePath() {
		return System.getenv(PokedexEnvironment.POKEDEX_MODEL_PATH.name());
	}

	@Override
	public String getHelpGifPath() {
		return System.getenv(PokedexEnvironment.POKEDEX_HELP_GIF_PATH.name());
	}

	@Override
	public String getZMoveClipPath() {
		return System.getenv(PokedexEnvironment.POKEDEX_Z_MOVE_PATH.name());
	}

	@Override
	public String getPokeFlexURL() {
		return System.getenv(PokedexEnvironment.POKEDEX_POKEFLEX_BASE_URL.name());
	}

	@Override
	public String getPatreonAuthToken() {
		return System.getenv(PokedexEnvironment.POKEDEX_PATREON_AUTH_TOKEN.name());
	}

	@Override
	public String getTcgApiKey() {
		return System.getenv(PokedexEnvironment.POKEDEX_TCG_API_KEY.name());
	}

}
