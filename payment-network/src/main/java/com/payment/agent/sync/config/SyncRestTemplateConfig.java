package com.payment.agent.sync.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class SyncRestTemplateConfig {

    @Bean("syncRestTemplate")
    public RestTemplate restTemplate(ApiKeyInterceptor interceptor){
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(interceptor);
        return template;
    }
}
