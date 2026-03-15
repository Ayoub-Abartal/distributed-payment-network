package com.payment.agent.sync.config;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import io.micrometer.common.lang.NonNull;

@Component
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class ApiKeyInterceptor  implements ClientHttpRequestInterceptor{

    private final ApiKeyHolder apiKeyHolder;

    public ApiKeyInterceptor(ApiKeyHolder apiKeyHolder){
        this.apiKeyHolder = apiKeyHolder;
    }

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request, 
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution
        )    throws IOException {
            
            String apiKey = apiKeyHolder.getApiKey();
            
            if(apiKey != null){
                request.getHeaders().add("X-API-Key",apiKey);
            }
            
            return execution.execute(request, body);
    }

    
}
