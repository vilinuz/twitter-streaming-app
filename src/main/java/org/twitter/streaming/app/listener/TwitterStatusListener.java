package org.twitter.streaming.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.config.TwitterStatusThreshold;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.twitter.streaming.app.reactive.TwitterStatusEventPublisher;
import org.twitter.streaming.app.reactive.TwitterStatusEventSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.twitter.streaming.app.transform.EntityTransformers.transformStatus;
import static org.twitter.streaming.app.transform.EntityTransformers.transformUser;

@Slf4j
@Component
public class TwitterStatusListener implements StatusListener {
    private TwitterStatusThreshold threshold;
    private TwitterStatusEventPublisher publisher;
    private AtomicInteger statusesCounter;
    private final Object lock = new Object();

    @Autowired
    public void setPublisher(TwitterStatusEventPublisher publisher) {
        this.publisher = publisher;
        this.publisher.subscribe(new TwitterStatusEventSubscriber());
        statusesCounter = new AtomicInteger(0);
    }

    @Autowired
    public void setThreshold(TwitterStatusThreshold threshold) {
        this.threshold = threshold;
    }

    @Override
    public void onStatus(Status status) {
        threshold.incrementCounter();

        if (threshold.reached()) {
            if (publisher.getSubscriber() != null) {
                publisher.getSubscriber().onComplete();
            }
            publisher.close();

            synchronized (lock) {
                lock.notify();
            }
        }

        if (Objects.nonNull(status) && Objects.nonNull(status.getUser())) {
            log.info("User " + status.getUser().getScreenName() + " posted status " + status.getText());
            TwitterStatusEvent event = TwitterStatusEvent.instance().
                    user(transformUser(status.getUser())).
                    message(transformStatus(status)).
                    build();
            publisher.submit(event);
            statusesCounter.incrementAndGet();
        }
    }

    public Object getLock() {
        return lock;
    }

    public TwitterStatusEventPublisher getPublisher() {
        return publisher;
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        log.info("Status with ID: " + statusDeletionNotice.getStatusId() + " has been deleted.");
    }

    @Override
    public void onTrackLimitationNotice(int i) {
        log.warn("Track Limitation Notice received. Number of undelivered tweets so far is: " + i);
    }

    @Override
    public void onScrubGeo(long l, long l1) {

    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {

    }

    @Override
    public void onException(Exception e) {
        log.warn("Something went wrong on Twitter.", e);
    }
}
