package com.rusanov.titkoff.managers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InstrumentsManager {

    @Autowired
    private OpenApi api;

    @PostConstruct
    public void init() {
        MarketInstrumentList marketInstrumentList = api.getMarketContext().getMarketStocks().join();
        marketInstrumentList.getInstruments()
                .forEach(marketInstrument -> {
                    String figi = marketInstrument.getFigi();
                    add(figi, marketInstrument);
                });
    }

    private Map<String, MarketInstrument> stocks = new HashMap<>();

    public void  add(String key, MarketInstrument value) {
        stocks.put(key,value);
    }

    public MarketInstrument findByFigi(String figi) {
        return stocks.get(figi);
    }

    public Collection<MarketInstrument> getInstruments() {
        return stocks.values();
    }
}
