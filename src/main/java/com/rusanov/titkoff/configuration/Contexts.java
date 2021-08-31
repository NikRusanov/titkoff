package com.rusanov.titkoff.configuration;

import com.rusanov.titkoff.bot.Bot;
import com.rusanov.titkoff.bot.ScrapperBot;
import com.rusanov.titkoff.managers.InstrumentsManager;
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

    @Bean(name= "configurationMbean")
    public ConfigurationControllerMBean getConfiguration() throws  Exception {
        ConfigurationControllerMBean configurationControllerMBean = new ConfigurationController();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(configurationControllerMBean, new ObjectName("config", "name", "config"));
        return  configurationControllerMBean;
    }

    @Bean
    public Bot getMainBot() {
        return new ScrapperBot();
    }
}
