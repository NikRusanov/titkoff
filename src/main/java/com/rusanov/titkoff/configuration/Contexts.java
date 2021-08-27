package com.rusanov.titkoff.configuration;

import com.rusanov.titkoff.instuments.InstrumentsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.StreamingContext;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutionException;

@Configuration
public class Contexts {

    private static final Logger logger = LoggerFactory.getLogger(Contexts.class);

    @Autowired
    private OpenApi api;

    @Autowired
    private InstrumentsManager instrumentsManager;

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
                    String figi = marketInstrument.getFigi();
                    instrumentsManager.add(figi, marketInstrument);
                    logger.info("=====================================================================");
                    logger.info("\nFIGI={}\nNAME={}\nTICKER={}\nLOT={}\nTYPE={}\nCURRENCY={}\n MIN_PRICE_INC={}\n MIN_QUANTITY={}",
                            figi,
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

    @Bean(name= "configurationMbean")
    public ConfigurationControllerMBean getConfiguration() throws  Exception {
        ConfigurationControllerMBean configurationControllerMBean = new ConfigurationController();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(configurationControllerMBean, new ObjectName("config", "name", "config"));
        return  configurationControllerMBean;
    }
}
