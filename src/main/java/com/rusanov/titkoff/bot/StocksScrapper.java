package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.AbstractVisitorViewer;
import com.rusanov.titkoff.configuration.ConfigurationControllerMBean;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.streaming.CandleInterval;
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.model.streaming.StreamingRequest;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Component
public class StocksScrapper extends AbstractVisitorViewer<Object, Void> {


    private static final Logger logger = LoggerFactory.getLogger(StocksScrapper.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private OpenApi api;

    @Autowired
    @Qualifier("marketStocks")
    private MarketInstrumentList marketInstruments;

    @Value("#{new Long('${maxSubscriptions}')}")
    public Long maxSubscription;

    private Map<String, List<StreamingEvent.Candle>> candles = new HashMap<>();

    @Autowired
    ConfigurationControllerMBean configuration;

    @PostConstruct
    public void init() {
        CompletableFuture<Void> stopNotifier = new CompletableFuture<Void>();
        Flowable<StreamingEvent> rxStreaming = Flowable.fromPublisher(api.getStreamingContext());
        Disposable rxSubscription = rxStreaming
                .doOnError(stopNotifier::completeExceptionally)
                .doOnComplete(() -> stopNotifier.complete(null))
                .forEach(this::handle);
        marketInstruments.getInstruments().stream().limit(maxSubscription).forEach(
                marketInstrument -> {
                    api.getStreamingContext().sendRequest(StreamingRequest.subscribeCandle(marketInstrument.getFigi(), CandleInterval._1MIN));
                });
    }

    public void handle(StreamingEvent.Candle candle) {

        String figi = candle.getFigi();
        List<StreamingEvent.Candle> candlesList;

        if (candles.containsKey(figi)) {
            candlesList = candles.get(figi);
        } else {
            candlesList = new ArrayList<>();
            candles.put(figi, candlesList);
        }
        candlesList.add(candle);
        removeOldCandles(figi);
        analyzeCandle(figi);
     //   logger.info("{}: {} {}", candle.getFigi(), String.valueOf(candle.getHighestPrice()), candle.getDateTime());
    }

    private void removeOldCandles(String figi) {
        List<StreamingEvent.Candle> candleList = this.candles.get(figi);
        candleList = candleList
                .stream()
                .filter(candle -> {
                            ZonedDateTime date = ZonedDateTime.now().minusMinutes(configuration.getTimeClearCandlesInterval());
                            return date.isBefore(candle.getDateTime());
                        }
                ).collect(Collectors.toList());
        this.candles.put(figi,candleList);

    }

    private void analyzeCandle(String figi) {
        List<StreamingEvent.Candle> candleList = this.candles.get(figi);
        int size = candles.size();
        if (size > configuration.getCandlesCount()) {
            candleList = candleList
                .stream()
                    .sorted(Comparator.comparing(StreamingEvent.Candle::getDateTime))
                    .collect(Collectors.toList());
            double firstElem = candleList.get(0).getOpenPrice().doubleValue();
            double lastElem = candleList.get(size - 1).getClosingPrice().doubleValue();
            double percent = (firstElem * 100.0) / lastElem;
            if (100.0 - percent > configuration.getSensibility()) {
                logger.debug("Perspective for trading {}", figi);
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void debug() {
        logger.info("sensibility: {} | time: {} ", configuration.getSensibility(), configuration.getTimeClearCandlesInterval());
    }


}
