package org.twitter.streaming.app.reactive;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;
import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.twitter.streaming.app.domain.TwitterUser;
import org.twitter.streaming.app.repository.TwitterStatusesJsonCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Flow;

@Slf4j
@Component
/**
 * The Twitter statuses subscriber. Here the important method is onNext where the subscriber receives event
 * from the publisher. See <code>{{@link TwitterStatusEventPublisher}}</code>.
 */
public class TwitterStatusEventSubscriber implements Flow.Subscriber<TwitterStatusEvent> {
    private Flow.Subscription subscription;
    private Multimap<TwitterUser, TwitterMessage> receivedStatusesByUser;
    private TwitterStatusesJsonCreator repository;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
        // Create Multimap so users and messages are sorted naturally in ascending order using their own comparators
        this.receivedStatusesByUser = Multimaps.synchronizedSortedSetMultimap(TreeMultimap.create());
        log.info("Twitter statuses subscriber ready to accept messages.");
    }

    @Override
    public void onNext(TwitterStatusEvent event) {
        log.debug("Received twitter event: " + event);
        receivedStatusesByUser.put(event.getUser(), event.getMessages().iterator().next());
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error encountered while consuming Twitter statuses.", t);
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        log.info("Completed consuming Twitter statuses.");
        subscription.cancel();
        try {
            StringWriter writer = new StringWriter();
            repository.toJson(receivedStatusesByUser, writer);
        } catch (IOException e) {
            log.error("Could not store to the given Writer", e);
        }
    }

    @Autowired
    public void setRepository(TwitterStatusesJsonCreator repository) {
        this.repository = repository;
    }

    public Multimap<TwitterUser, TwitterMessage> getReceivedStatusesByUser() {
        return receivedStatusesByUser;
    }
}
