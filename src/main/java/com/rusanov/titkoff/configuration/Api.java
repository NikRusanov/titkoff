package com.rusanov.titkoff.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.SandboxRegisterRequest;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

@Configuration
@PropertySource("classpath:token.properties")
@EnableScheduling
public class Api {

    @Bean
    public OpenApi getApi(@Value("${token}") String token, @Value("#{new Boolean('${isSandbox}')}") Boolean isSandbox) {
        OpenApi api = new OkHttpOpenApi(token, isSandbox);
        if (api.isSandboxMode())
            api.getSandboxContext().performRegistration(new SandboxRegisterRequest()).join();
        return api;
    }
}




