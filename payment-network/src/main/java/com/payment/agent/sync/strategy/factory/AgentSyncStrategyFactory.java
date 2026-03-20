package com.payment.agent.sync.strategy.factory;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.EntityType;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.shared.sync.strategy.entity.EntitySyncStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory for creating entity sync strategies dynamically based on configuration.
 * 
 * This factory uses Spring's ApplicationContext to retrieve strategy beans by name,
 * enabling runtime strategy selection without hardcoded if-else logic.
 * 
 * Bean Naming Convention:
 * {strategyType}{EntityType}SyncStrategy
 * 
 * Examples:
 * - HTTP + TRANSACTION → httpTransactionSyncStrategy
 * - KAFKA + CUSTOMER → kafkaCustomerSyncStrategy
 * - HYBRID + TRANSACTION → hybridTransactionSyncStrategy
 * 
 * Usage:
 * EntitySyncStrategy<TransactionEvent> strategy = 
 *     factory.getStrategy(EntityType.TRANSACTION, SyncStrategyType.HTTP);
 * 
 * @see EntityType
 * @see SyncStrategyType
 * @see EntitySyncStrategy
 */
@Component
@Slf4j
public class AgentSyncStrategyFactory {

    private ApplicationContext applicationContext;

    public AgentSyncStrategyFactory(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends SyncEvent> EntitySyncStrategy<T> getStrategy(EntityType entityType, SyncStrategyType strategyType){
        
        // 1 build bean name
        String beanName = buildBeanName(entityType, strategyType);

        // 2. Log what you're looking for (debug level)
        log.debug("Looking for strategy bean: {}", beanName);
        
        //3 Try to get bean name from Spring Context 
        try{
            return applicationContext.getBean(beanName, EntitySyncStrategy.class);
        }catch(NoSuchBeanDefinitionException e){
            
            log.error("Strategy not found: {}. Available strategies must follow naming convention.", beanName);
            
            throw new IllegalStateException(
                "No such strategy found for : "+ entityType + " with " + strategyType +
                ". Expected bean name : " + beanName,e
            );
        }
    }

    private String buildBeanName(EntityType entityType,SyncStrategyType strategyType){
        return strategyType.name().toLowerCase()
            +  capitalize(entityType.name())
            +   "SyncStrategy";
    }

    private String capitalize(String s){
        if(s == null || s.isEmpty()){
            return s;
        }

        String loweString = s.toLowerCase();
        return loweString.substring(0,1).toUpperCase() + loweString.substring(1);
    }

}
