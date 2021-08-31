package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.AbstractVisitorViewer;
import com.rusanov.titkoff.managers.HandlersManager;
import com.rusanov.titkoff.managers.InstrumentsManager;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.streaming.CandleInterval;
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.model.streaming.StreamingRequest;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Component
public class StocksInterceptor extends AbstractVisitorViewer<Object, Void> {

    @Autowired
    private OpenApi api;

    @Autowired
    private InstrumentsManager instrumentsManager;

    @Autowired
    private HandlersManager handlersManager;

    @Autowired
    @Qualifier("mainBot")
    private Bot bot;

    @Value("#{new Long('${maxSubscriptions}')}")
    public Long maxSubscription;

    private static Logger logger = LoggerFactory.getLogger(StocksInterceptor.class);

    @PostConstruct
    public void init() {
        CompletableFuture<Void> stopNotifier = new CompletableFuture<Void>();
        Flowable<StreamingEvent> rxStreaming = Flowable.fromPublisher(api.getStreamingContext());
        Disposable rxSubscription = rxStreaming
                .doOnError(stopNotifier::completeExceptionally)
                .doOnComplete(() -> stopNotifier.complete(null))
                .forEach(this::handle);
        instrumentsManager.getInstruments().stream().limit(maxSubscription).forEach(
                marketInstrument -> {
                    handlersManager.subscribe(marketInstrument.getFigi(), bot);
                    api.getStreamingContext().sendRequest(StreamingRequest.subscribeCandle(marketInstrument.getFigi(), CandleInterval._1MIN));
                });
    }

    public void handle(StreamingEvent.Candle candle) {
        String figi = candle.getFigi();
        handlersManager.publish(figi, candle);
    }
}
