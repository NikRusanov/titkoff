package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.AbstractVisitorViewer;
import com.rusanov.titkoff.api.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.StreamingContext;
import ru.tinkoff.invest.openapi.model.rest.Candle;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.SearchMarketInstrument;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Component
public class Scheduler extends AbstractVisitorViewer<Object,Void> {


    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    private Map<String, List<Candle>> candles;

    public Scheduler() {
        this(null);
        candles = new HashMap<>();
    }
    /**
     * Base constructor
     *
     * @param subHandler - sublayer whose method will be called if no method is found to process the current object in the current layer
     */
    public Scheduler(Handler<Object, Void> subHandler) {
        super(subHandler);
    }

    public void handle(Candle candle) {
        String figi = candle.getFigi();
        List<Candle> candlesList;

        if (candles.containsKey(figi)) {
            candlesList = candles.get(figi);
        } else  {
            candlesList = new ArrayList<>();
            candles.put(figi,candlesList);
        }
        candlesList.add(candle);
        log.info(String.valueOf(candle.getTime()));
    }

}
