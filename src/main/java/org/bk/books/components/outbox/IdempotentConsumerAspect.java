package org.bk.books.components.outbox;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bk.books.domain.entity.common.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class IdempotentConsumerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentConsumerAspect.class);
    private final ConsumerMessageLogStore consumerMessageLogStore;

    public IdempotentConsumerAspect(ConsumerMessageLogStore consumerMessageLogStore) {
        this.consumerMessageLogStore = consumerMessageLogStore;
    }

    @Around("@annotation(eventListener)")
    @Transactional
    public Object aroundEventListener(ProceedingJoinPoint joinPoint, ReliableEventListener eventListener) throws Throwable {
        BaseEvent event = extractEvent(joinPoint.getArgs());
        String consumerId = resolveConsumerId(eventListener, joinPoint);
        boolean alreadyProcessed = consumerMessageLogStore.alreadyProcessed(event.eventId(), consumerId);
        if (alreadyProcessed) {
            LOGGER.info("Skipping duplicate event delivery for consumerId={} eventId={}", consumerId, event.eventId());
            return null;
        }

        try {
            Object returnValue = joinPoint.proceed();
            consumerMessageLogStore.markProcessed(consumerId, event.eventId());
            return returnValue;
        }catch (Throwable throwable) {
            throw throwable;
        }
    }

    private BaseEvent extractEvent(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof BaseEvent baseEvent) {
                return baseEvent;
            }
        }
        throw new IllegalStateException("Should not happen!");
    }

    private String resolveConsumerId(ReliableEventListener eventListener, ProceedingJoinPoint joinPoint) {
        return eventListener.id();
    }
}
