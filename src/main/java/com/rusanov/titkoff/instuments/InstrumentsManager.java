package com.rusanov.titkoff.instuments;


import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;

import java.rmi.NoSuchObjectException;
import java.util.HashMap;
import java.util.Map;

@Component
public class InstrumentsManager {
    private Map<String, MarketInstrument> stocks;

    public InstrumentsManager() {
        stocks = new HashMap<>();
    }

    public void  add(String key, MarketInstrument value) {
        stocks.put(key,value);
    }


    public MarketInstrument findByFigi(String figi) {
        return stocks.get(figi);
    }
}
