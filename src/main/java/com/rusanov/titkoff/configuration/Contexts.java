package com.rusanov.titkoff.configuration;

import com.rusanov.titkoff.bot.Scheduler;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
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
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.model.streaming.StreamingRequest;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class Contexts {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    @Autowired
    private OpenApi api;

    @Autowired
    private Scheduler scheduler;

    @Bean(name = "marketContext")
    public MarketContext getMarketContext(){
        return api.getMarketContext();
    }

    @Bean(name = "streamingContext")
    public StreamingContext getStreamingContext(){
        return api.getStreamingContext();
    }

    @Bean(name = "marketStocks")
    public MarketInstrumentList getMarketStocks() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> stopNotifier = new CompletableFuture<Void>();
        @NonNull Flowable<StreamingEvent> rxStreaming = Flowable.fromPublisher(api.getStreamingContext());
        @NonNull Disposable rxSubscription = rxStreaming
                .doOnError(stopNotifier::completeExceptionally)
                .doOnComplete(() -> stopNotifier.complete(null))
                .forEach(event -> {
                    scheduler.handle(event);
                });
        MarketInstrumentList marketInstrumentList = getMarketContext().getMarketStocks().join();
        marketInstrumentList.getInstruments()
                .forEach(marketInstrument -> {
                    try {
                        Optional<SearchMarketInstrument> instrument = getMarketContext().searchMarketInstrumentByFigi(marketInstrument.getFigi()).join();
                        SearchMarketInstrument searchMarketInstrument;
                        if (instrument.isPresent()) {
                            searchMarketInstrument = instrument.get();
                        } else  {
                            log.info("market instrument not found");
                            return;
                        }
                        log.info("=====================================================================");
                        log.info("\nFIGI={}\nNAME={}\nTICKER={}\nLOT={}\nTYPE={}\nCURRENCY={}\n MIN_PRICE_INC={}\n MIN_QUANTITY={}\nLOT={}",
                                marketInstrument.getFigi(),
                                marketInstrument.getName(),
                                marketInstrument.getTicker(),
                                marketInstrument.getLot(),
                                marketInstrument.getType().getValue(),
                                marketInstrument.getCurrency().getValue(),
                                marketInstrument.getMinPriceIncrement(),
                                marketInstrument.getMinQuantity(),
                                searchMarketInstrument.getLot()
                        );
                        log.info("=====================================================================");
                        api.getStreamingContext().sendRequest(StreamingRequest.subscribeCandle(searchMarketInstrument.getFigi(), CandleInterval._1MIN ));
                    } catch (Exception ex) {
                            log.error(ex.getMessage());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                });
        stopNotifier.join();
        return marketInstrumentList;
    }



    @Bean(name = "marketEtfs")
    public  MarketInstrumentList getMarketEtfs() throws ExecutionException, InterruptedException {
        return  getMarketContext().getMarketEtfs().get();
    }
    @Bean(name = "marketBonds")
    public  MarketInstrumentList getMarketBonds() throws ExecutionException, InterruptedException {
        return getMarketContext().getMarketBonds().get();
    }

}
