package com.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentNetworkApplication {
    public static void main(String[] args){
        SpringApplication app = new SpringApplication(PaymentNetworkApplication.class);
        app.setAdditionalProfiles(getActiveProfile(args));

        app.run(args);
    }

    private static String getActiveProfile(String[] args){
        for(String arg : args ) {
            if (arg.startsWith("--spring.profiles.active=")) {
                return arg.substring("--spring.profiles.active=".length());
            }
        }
            // Check environment variable
            String profileFromEnv = System.getProperty("SPRING_PROFILES_ACTIVE");
            if (profileFromEnv != null && !profileFromEnv.isEmpty()) {
                return profileFromEnv;
            }

            // Default to master
            return "master";
        }
}

