package com.rusanov.titkoff.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.SandboxContext;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

import java.math.BigDecimal;

@Configuration
public class Contexts {

    private final  String SANDBOX_ACCOUNT_ID = "B130SNW24M";
    private static final Logger logger = LoggerFactory.getLogger(Contexts.class);

    @Bean
    public OpenApi getApi(@Value("${token}") String token, @Value("#{new Boolean('${isSandbox}')}") Boolean isSandbox) {
        OpenApi api = new OkHttpOpenApi(token, isSandbox);
        if (api.isSandboxMode()) {
            sandboxAccount(api);
        }
        return api;
    }

    @Bean(name = "sandBoxAccount")
    public SandboxAccount sandboxAccount(OpenApi api)  {
        SandboxRegisterRequest request = new SandboxRegisterRequest();
        request.setBrokerAccountType(BrokerAccountType.TINKOFF);
        SandboxContext context = api.getSandboxContext();
        SandboxAccount sandboxAccount = context.performRegistration(request).join().brokerAccountId(SANDBOX_ACCOUNT_ID);
        context.setCurrencyBalance(new SandboxSetCurrencyBalanceRequest().balance(new BigDecimal(10000)).currency(SandboxCurrency.USD),SANDBOX_ACCOUNT_ID);
        return sandboxAccount;
    }

    @Bean
    public PortfolioContext getPortfolioContext(OpenApi api) {
        return  api.getPortfolioContext();
    }

    @Bean(name = "orderContext")
    public OrdersContext getOrderContext(OpenApi api) {
        return api.getOrdersContext();
    }


}
