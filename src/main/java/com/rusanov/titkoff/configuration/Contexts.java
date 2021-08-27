package com.rusanov.titkoff.configuration;

import com.rusanov.titkoff.bot.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.StreamingContext;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.SearchMarketInstrument;
import ru.tinkoff.invest.openapi.model.streaming.CandleInterval;
import ru.tinkoff.invest.openapi.model.streaming.StreamingRequest;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Configuration
public class Contexts {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private OpenApi api;

    @Bean(name = "marketContext")
    public MarketContext getMarketContext() {
        return api.getMarketContext();
    }

    @Bean(name = "streamingContext")
    public StreamingContext getStreamingContext() {
        return api.getStreamingContext();
    }

    @Bean(name = "marketStocks")
    public MarketInstrumentList getMarketStocks() throws ExecutionException, InterruptedException {
        MarketInstrumentList marketInstrumentList = getMarketContext().getMarketStocks().join();
        marketInstrumentList.getInstruments()
                .forEach(marketInstrument -> {
                    logger.info("=====================================================================");
                    logger.info("\nFIGI={}\nNAME={}\nTICKER={}\nLOT={}\nTYPE={}\nCURRENCY={}\n MIN_PRICE_INC={}\n MIN_QUANTITY={}",
                            marketInstrument.getFigi(),
                            marketInstrument.getName(),
                            marketInstrument.getTicker(),
                            marketInstrument.getLot(),
                            marketInstrument.getType().getValue(),
                            marketInstrument.getCurrency().getValue(),
                            marketInstrument.getMinPriceIncrement(),
                            marketInstrument.getMinQuantity()
                    );
                    logger.info("=====================================================================");
                });
        return marketInstrumentList;
    }


    @Bean(name = "marketEtfs")
    public MarketInstrumentList getMarketEtfs() throws ExecutionException, InterruptedException {
        return getMarketContext().getMarketEtfs().get();
    }

    @Bean(name = "marketBonds")
    public MarketInstrumentList getMarketBonds() throws ExecutionException, InterruptedException {
        return getMarketContext().getMarketBonds().get();
    }

}
