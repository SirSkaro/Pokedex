package skaro.pokedex.data_processor.formatters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jetty.util.MultiMap;

import com.google.common.collect.Lists;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.card.Card;
import skaro.pokeflex.objects.card.Card_;
import skaro.pokeflex.objects.card.Cards;

public class CardResponseFormatter implements ResponseFormatter, PokedexServiceConsumer 
{
	private PokedexServiceManager services;
	
	public CardResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException 
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.EMOJI);
	}
	
	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		if(data.containsKey(Card.class.getName()))
			return formatSingleCard(((Card)data.getValue(Card.class.getName(), 0)).getCard(), builder);
		return formatCardSearch((Cards)data.getValue(Cards.class.getName(), 0), builder);
			
	}

	private Response formatSingleCard(Card_ card, EmbedCreateSpec builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		
		builder.setImage(card.getImageUrlHiRes());
		if(card.getTypes() != null)
			builder.setColor(colorService.getColorForCardType(card.getTypes()));
		else
			builder.setColor(colorService.getCardColor());
		
		Response response = new Response();
		response.setEmbed(builder);
		return response;
	}
	
	private Response formatCardSearch(Cards cards, EmbedCreateSpec builder) 
	{
		if(cards.getCards().size() == 1)
			return formatSingleCard(cards.getCards().get(0), builder);
		
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Map<String, Cards> cardsBySeries = groupBySeries(cards);
		int numberOfSeries = cardsBySeries.size();
		int maxSeries = 6;
		int seriesToShow = numberOfSeries <= maxSeries ? numberOfSeries : maxSeries;
		
		response.addToReply(String.format("**__%s results from %d series__**", cards.getCards().size(), cardsBySeries.size()));
		
		Iterator<Entry<String, Cards>> seriesIterator = cardsBySeries.entrySet().iterator();
		for(int i = 0; i < seriesToShow; i++) {
			Entry<String, Cards> series = seriesIterator.next();
			builder.addField(series.getKey(), cardsToList(series.getValue()), true);
		}
		
		if(numberOfSeries > maxSeries)
			builder.addField(String.format("... and more from %d series", numberOfSeries - seriesToShow), seriesToList(seriesIterator), true);
		
		builder.setColor(colorService.getCardColor());
		response.setEmbed(builder);
		return response;
	}
	
	private String seriesToList(Iterator<Entry<String, Cards>> series) {
		Iterable<Entry<String, Cards>> iterable = () -> series;
		Stream<Entry<String, Cards>> targetStream = StreamSupport.stream(iterable.spliterator(), false);
		
		return targetStream
			.map(seriesEntry -> seriesEntry.getKey())
			.collect(Collectors.joining(", "));
	}
	
	private Map<String, Cards> groupBySeries(Cards cards) 
	{
		Map<String, Cards> result = new HashMap<>();
		cards.getCards().forEach(card -> {
			String series = card.getSeries();
			if(result.containsKey(series)) {
				result.get(series).getCards().add(card);
			} else {
				Cards deck = new Cards();
				deck.setCards(Lists.newArrayList(card));
				result.put(series, deck);
			}
		});
		
		return result;
	}
	
	private String cardsToList(Cards cards) 
	{
		int maxCards = 5;
		int numberOfCards = cards.getCards().size();
		int cardsToShow = numberOfCards <= maxCards ? numberOfCards : maxCards;
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		List<String> cardList = new ArrayList<>();
		for(int i = 0; i < cardsToShow; i++) {
			Card_ card = cards.getCards().get(i);
			cardList.add(String.format("%s **%s**\n#%s from __%s__\n(`%s`)", emojiService.getCardSetEmoji(card.getSetCode()), card.getName(), card.getNumber(), card.getSet(), card.getId()));
		}
		
		if(numberOfCards > cardsToShow)
			cardList.add(String.format("... and %d more", numberOfCards - cardsToShow));
		
		return String.join("\n", cardList);
	}
	
}
