package com.rusanov.titkoff.configuration;

import com.rusanov.titkoff.bot.AnalyzeBot;
import com.rusanov.titkoff.bot.Bot;
import com.rusanov.titkoff.bot.ScrapperBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.SandboxContext;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

@Configuration
@PropertySource("classpath:token.properties")
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class MainConfiguration {

    @Bean(name= "configurationMbean")
    public ConfigurationControllerMBean getConfiguration() throws  Exception {
        ConfigurationControllerMBean configurationControllerMBean = new ConfigurationController();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(configurationControllerMBean, new ObjectName("config", "name", "config"));
        return  configurationControllerMBean;
    }

    @Bean("mainBot")
    public Bot getMainBot() {
        ScrapperBot scrapperBot = new ScrapperBot();
        scrapperBot.setSubHandler(getAnalyzeBot());
        return scrapperBot;
    }

    @Bean("analyzeBot")
    public Bot getAnalyzeBot() {
        return new AnalyzeBot();
    }
}




