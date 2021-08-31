package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.Handler;
import com.rusanov.titkoff.configuration.ConfigurationControllerMBean;
import com.rusanov.titkoff.managers.HandlersManager;
import com.rusanov.titkoff.managers.InstrumentsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ScrapperBot extends Bot implements Handler<Object,Void> {

    @Autowired
    private ConfigurationControllerMBean configuration;

    @Autowired
    private InstrumentsManager instrumentsManager;


    private Map<String, List<StreamingEvent.Candle>> candles = new HashMap<>();

    private static Logger logger = LoggerFactory.getLogger(ScrapperBot.class);

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
        if ((candles.get(figi) != null) && !(candles.get(figi).isEmpty()))
            analyzeCandle(figi);
       // logger.info("{}: {} {}", candle.getFigi(), String.valueOf(candle.getHighestPrice()), candle.getDateTime());
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
        this.candles.put(figi, candleList);
    }

    private void analyzeCandle(String figi) {
        List<StreamingEvent.Candle> candleList = this.candles.get(figi);
        int size = candleList.size();
        if (size > configuration.getCandlesCount()) {
            candleList = candleList
                    .stream()
                    .sorted(Comparator.comparing(StreamingEvent.Candle::getDateTime))
                    .collect(Collectors.toList());
            double openPrice = candleList.get(0).getOpenPrice().doubleValue();
            double closingPrice = candleList.get(size - 1).getClosingPrice().doubleValue();
            double percent = (openPrice * 100.0) / closingPrice;
            if (closingPrice > configuration.getStockMinimumPrice()
                    && closingPrice < configuration.getStockMaximumPrice()
                    && 100.0 - percent > configuration.getSensibility()) {
                MarketInstrument marketInstrument = instrumentsManager.findByFigi(figi);
                logger.info("Perspective for trading {} with name {}. Open price: {} Closing price: {}", figi, marketInstrument.getName(), openPrice, closingPrice);
                handlersManager.unsubscribe(figi,this);
                Handler handler = getSubHandler();
                handler.handle(marketInstrument);
            }
        }
    }
}
