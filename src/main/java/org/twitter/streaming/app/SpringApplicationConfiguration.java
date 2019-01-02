package org.twitter.streaming.app;

import org.twitter.streaming.app.config.TwitterConfig;
import org.twitter.streaming.app.config.TwitterStatusThreshold;
import org.twitter.streaming.app.listener.TwitterStatusListener;
import org.twitter.streaming.app.reactive.TwitterStatusEventPublisher;
import org.twitter.streaming.app.reactive.TwitterStatusEventSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.interview.oauth.twitter")
public class SpringApplicationConfiguration {
    public static final String CONFIG = "config";
    public static final String THRESHOLD = "threshold";
    public static final String PUBLISHER = "publisher";
    public static final String SUBSCRIBER= "subscriber";
    public static final String LISTENER = "listener";

    @Bean(CONFIG)
    public TwitterConfig getConfiguration() {
        return new TwitterConfig();
    }

    @Bean(THRESHOLD)
    public TwitterStatusThreshold getThreshold() {
        return new TwitterStatusThreshold();
    }

    @Bean(PUBLISHER)
    public TwitterStatusEventPublisher getPublisher() {
        return new TwitterStatusEventPublisher(new TwitterConfig());
    }

    @Bean(SUBSCRIBER)
    public TwitterStatusEventSubscriber getSubscriber() {
        return new TwitterStatusEventSubscriber();
    }

    @Bean(LISTENER)
    public TwitterStatusListener getListener() {
        return new TwitterStatusListener();
    }
}
