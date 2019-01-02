package org.twitter.streaming.app.listener;

import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.SpringApplicationConfiguration;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterUser;
import org.twitter.streaming.app.reactive.TwitterStatusEventSubscriber;
import org.twitter.streaming.app.repository.TwitterStatusesJsonCreator;
import org.twitter.streaming.app.util.StatusMockCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import twitter4j.Status;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TwitterStatusListenerTest {

    private StatusMockCreator creator;
    private TwitterStatusListener listener;

    @BeforeEach
    void setUp() {
        creator = StatusMockCreator.instance();
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);
        listener = context.getBean(SpringApplicationConfiguration.LISTENER, TwitterStatusListener.class);
        ((TwitterStatusEventSubscriber)listener.getPublisher().getSubscriber()).setRepository(new TwitterStatusesJsonCreator());
    }

    @Test
    void testOnStatus_FinishOnNumberOfStatusesThreshold() {
        callOnStatusEvery(Duration.ofMillis(50));
        Multimap<TwitterUser, TwitterMessage> usersWithStatuses = ((TwitterStatusEventSubscriber) listener.getPublisher().getSubscriber()).getReceivedStatusesByUser();
        assertEquals(usersWithStatuses.size(), 100);
    }

    @Test
    void testOnStatus_FinishOnTimeElapsedThreshold() {
        assertTimeout(Duration.ofSeconds(31), () -> callOnStatusEvery(Duration.ofMillis(500)));
    }

    /**
     * Method to call <code>{@link TwitterStatusListener#onStatus(Status)}</code> on
     * a given interval.
     * @param numberOfMillis - the given interval in milliseconds
     */
    private void callOnStatusEvery(final Duration numberOfMillis) {
        Runnable callOnStatusTask = () -> {
            List<Status> statuses = creator.mockStatuses(101);
            for (Status status : statuses) {
                try {
                    Thread.sleep(numberOfMillis.toMillis());
                } catch (InterruptedException e) {
                    log.info("Interrupted. Nothing to worry.");
                }
                log.info("User '" + status.getUser().getName() + "' created status: '" + status.getText() + "'");
                listener.onStatus(status);
            }
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(callOnStatusTask);
        waitToFinish();
    }

    private void waitToFinish() {
        try {
            synchronized (listener.getLock()) {
                listener.getLock().wait();
            }
        } catch (InterruptedException e) {
            log.error("Twitter streaming process interrupted.", e);
        }
    }
}