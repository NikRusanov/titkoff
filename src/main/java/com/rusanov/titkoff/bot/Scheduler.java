package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.AbstractVisitorViewer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.streaming.CandleInterval;
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.model.streaming.StreamingRequest;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Component
public class Scheduler extends AbstractVisitorViewer<Object, Void> {


    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private OpenApi api;

    @Autowired
    @Qualifier("marketStocks")
    private MarketInstrumentList marketInstruments;

    @Value("#{new Long('${maxSubscriptions}')}")
    public Long maxSubscription;

    private Map<String, List<StreamingEvent.Candle>> candles = new HashMap<>();

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
                    api.getStreamingContext().sendRequest(StreamingRequest.subscribeCandle(marketInstrument.getFigi(), CandleInterval._10MIN));
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
        logger.info("{}: {} {}", candle.getFigi(), String.valueOf(candle.getHighestPrice()), candle.getDateTime());
    }

}
