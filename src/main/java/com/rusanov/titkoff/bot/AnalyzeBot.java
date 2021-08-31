package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.model.rest.*;

public class AnalyzeBot extends Bot implements Handler<Object,Void> {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeBot.class);

    @Autowired
    OpenApi api;

    @Autowired
    OrdersContext ordersContext;

    @Autowired
    SandboxAccount sandboxAccount;

    @Autowired
    PortfolioContext portfolioContext;

    public void handle(MarketInstrument marketInstrument) {
        drainAllMoney(marketInstrument.getFigi());
    }

    public void drainAllMoney(String figi) {
        String accountId = sandboxAccount.getBrokerAccountId();
        PlacedMarketOrder marketOrder = ordersContext.placeMarketOrder(figi, new MarketOrderRequest().lots(100).operation(OperationType.BUY), accountId).join();
        portfolioContext.getPortfolioCurrencies(accountId).join().getCurrencies().forEach( (currencyPosition) -> {
                logger.info("current balance | RU {} | USD {}", currencyPosition.getCurrency(), currencyPosition.getBalance());
                }
        );

        logger.info("order status: {}, ", marketOrder.getStatus());

    }
}
