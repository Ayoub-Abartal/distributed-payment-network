package com.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentNetworkApplication {
    public static void main(String[] args){
        SpringApplication.run(PaymentNetworkApplication.class);
    }
}
