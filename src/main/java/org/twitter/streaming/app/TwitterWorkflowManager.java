package org.twitter.streaming.app;

import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.config.TwitterConfig;
import org.twitter.streaming.app.listener.TwitterStatusListener;
import org.twitter.streaming.app.reactive.TwitterStatusEventPublisher;
import org.twitter.streaming.app.reactive.TwitterStatusEventSubscriber;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the entry point of the application.
 */
@Slf4j
public class TwitterWorkflowManager {
    private TwitterConfig config;
    private TwitterStatusEventPublisher publisher;
    private TwitterStatusEventSubscriber subscriber;
    private TwitterStatusListener listener;

    private TwitterWorkflowManager() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);
        this.config = context.getBean(SpringApplicationConfiguration.CONFIG, TwitterConfig.class);
        this.publisher = context.getBean(SpringApplicationConfiguration.PUBLISHER, TwitterStatusEventPublisher.class);
        this.subscriber = context.getBean(SpringApplicationConfiguration.SUBSCRIBER, TwitterStatusEventSubscriber.class);
        this.listener = context.getBean(SpringApplicationConfiguration.LISTENER, TwitterStatusListener.class);
    }

    /**
     * Method to launch the Twitter streaming process.
     */
    private void start() {
        log.info("Setting up the Twitter stream process..");
        TwitterStream stream = new TwitterStreamFactory().getInstance(config.getAuthorisationConfig());
        ExecutorService service = Executors.newSingleThreadExecutor();

        try {
            Runnable twitterStreamingTask = createTwitterStreamingTask(stream);
            service.submit(twitterStreamingTask);
            waitToFinish();
        } finally {
            log.info("Done! Cleaning up the streaming process and shutting down..");
            stream.cleanUp();
            stream.shutdown();
            service.shutdown();
            log.info("Done! ");        }
    }

    /**
     * Creates <code>{@link Runnable}</code> task for launching the Twitter streaming.
     * @param stream - the <code>{@link TwitterStream}</code>
     * @return <code>{@link Runnable}</code> task
     */
    private Runnable createTwitterStreamingTask(final TwitterStream stream) {
        return  () -> {
            publisher.subscribe(subscriber);
            log.info("Setting up the Twitter filter query..");
            FilterQuery filterQuery = new FilterQuery();
            filterQuery.track(config.getTwitterFilterKeywords());
            stream.addListener(listener);
            log.info("Starting the Twitter streaming process..");
            stream.filter(filterQuery);
            log.info("Waiting for Twitter streaming process to finish..");
        };

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

    public static void main(String[] args) {
        TwitterWorkflowManager manager = new TwitterWorkflowManager();
        manager.start();
    }
}
