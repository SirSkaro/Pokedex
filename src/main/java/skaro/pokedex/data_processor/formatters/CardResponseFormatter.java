package skaro.pokedex.data_processor.formatters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
		if(cards.getCards().isEmpty())
			return formatNoCardResponse();

		if(cards.getCards().size() == 1)
			return formatSingleCard(cards.getCards().get(0), builder);

		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Map<String, Cards> cardsBySeries = groupAndSortBySeries(cards);
		int numberOfSeries = cardsBySeries.size();
		int maxSeries = 6;
		int seriesToShow = numberOfSeries <= maxSeries ? numberOfSeries : maxSeries;
		int remainingCards = cards.getCards().size();

		response.addToReply(String.format("**__%s results from %d series__**", remainingCards == 100 ? "100+" : remainingCards, cardsBySeries.size()));

		Iterator<Entry<String, Cards>> seriesIterator = cardsBySeries.entrySet().iterator();
		for(int i = 0; i < seriesToShow; i++) {
			Entry<String, Cards> series = seriesIterator.next();
			String fieldTitle = String.format("**%d cards from __%s__**", series.getValue().getCards().size(), series.getKey());
			builder.addField(fieldTitle, cardsToList(series.getValue()), true);
			remainingCards -= series.getValue().getCards().size();
		}

		if(numberOfSeries > maxSeries)
			builder.addField(String.format("**... and %d more cards from %d series**", remainingCards, (numberOfSeries - seriesToShow)), seriesToList(seriesIterator), true);

		builder.setColor(colorService.getCardColor());
		response.setEmbed(builder);
		return response;
	}

	private Response formatNoCardResponse() {
		Response response = new Response();
		response.addToReply("Your card search yielded no results!");

		return response;
	}

	private String seriesToList(Iterator<Entry<String, Cards>> series) {
		Iterable<Entry<String, Cards>> iterable = () -> series;
		Stream<Entry<String, Cards>> targetStream = StreamSupport.stream(iterable.spliterator(), false);

		return targetStream
				.map(seriesEntry -> seriesEntry.getKey())
				.collect(Collectors.joining(", "));
	}

	private Map<String, Cards> groupAndSortBySeries(Cards cards) 
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

		return sortByCardCount(result);
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

		return String.join("\n\n", cardList);
	}

	private Map<String, Cards> sortByCardCount(Map<String, Cards> unsortMap) 
	{
		List<Map.Entry<String, Cards>> list =
				new LinkedList<Map.Entry<String, Cards>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Cards>>() {
			public int compare(Map.Entry<String, Cards> o1,
					Map.Entry<String, Cards> o2) {
				return o2.getValue().getCards().size() - o1.getValue().getCards().size();
			}
		});

		Map<String, Cards> sortedMap = new LinkedHashMap<String, Cards>();
		for (Map.Entry<String, Cards> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}

}
