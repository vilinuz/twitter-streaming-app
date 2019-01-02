package org.twitter.streaming.app.reactive;

import org.twitter.streaming.app.SpringApplicationConfiguration;
import org.twitter.streaming.app.util.StatusMockCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class TwitterPublisherSubscriberTest {
    private TwitterStatusEventSubscriber subscriber;
    private TwitterStatusEventPublisher publisher;
    private StatusMockCreator statusMockCreator;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);
        this.publisher = context.getBean("publisher", TwitterStatusEventPublisher.class);
        this.subscriber = context.getBean("subscriber", TwitterStatusEventSubscriber.class);
        statusMockCreator = StatusMockCreator.instance();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSubscriberReceivedTwitterStatus() {
        publisher.subscribe(subscriber);
        publisher.submit(statusMockCreator.mockTwitterEvent());
        publisher.close();

        await().atMost(1000, TimeUnit.MILLISECONDS).until(this::subscriberGotTwitterEvent);
    }

    private boolean subscriberGotTwitterEvent() {
        return subscriber.getReceivedStatusesByUser().containsValue(statusMockCreator.mockTwitterEvent().getMessages().iterator().next());
    }
}
