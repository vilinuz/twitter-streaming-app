package org.twitter.streaming.app.reactive;

import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.config.TwitterConfig;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.twitter.streaming.app.listener.TwitterStatusListener;
import twitter4j.Status;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Component
/**
 * The Twitter Events publisher. It's hooked to <code>{@link TwitterStatusListener#onStatus(Status)}</code>.
 * It submits events to the <code>{@link TwitterStatusEventSubscriber}</code> to be processed further.
 */
public class TwitterStatusEventPublisher implements Flow.Publisher<TwitterStatusEvent> {
    private SubmissionPublisher<TwitterStatusEvent> publisher;
    private TwitterConfig config;
    private Flow.Subscriber<? super TwitterStatusEvent> subscriber;

    public TwitterStatusEventPublisher(final TwitterConfig config) {
        this.config = config;
        publisher = new SubmissionPublisher<>();
    }

    @Autowired
    public void setConfig(TwitterConfig config) {
        this.config = config;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super TwitterStatusEvent> subscriber) {
        this.subscriber = subscriber;
        publisher.subscribe(subscriber);
    }

    public void close() {
        publisher.close();
    }

    public Flow.Subscriber<? super TwitterStatusEvent> getSubscriber() {
        return subscriber;
    }

    public int getNumberOfSubscribers() {
        return publisher.getNumberOfSubscribers();
    }

    /**
     * Submits <code>{@link TwitterStatusEvent}</code> with the user and a single message as it appeared in
     * the Twitter stream.
     * @param event - the Twitter event as it appeared in the Twitter stream
     */
    public void submit(final TwitterStatusEvent event) {
        if (filterEvent(event)) {
            try {
                publisher.submit(event);
            } catch (RuntimeException e) {
                // We might get interrupted when particular threshold has been reached.
                // We don't need to worry much in this situation. Just log it.
                log.warn("Published has been interrupted. Reason: " + e.getMessage());
            }

        }
    }

    /**
     * Filters event with a pre-configured keyword. Theis filter is case-insensitive.
     * @param event - the Twitter event
     * @return true if event contains given keyword, else false
     */
    private boolean filterEvent(final TwitterStatusEvent event) {
        return containsIgnoreCase(
                event.getMessages().iterator().next().getText(),
                config.getTwitterFilterKeywords());
    }
}
