package org.twitter.streaming.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.twitter.streaming.app.config.TwitterConfig;
// import org.twitter.streaming.app.config.TwitterStatusThreshold; // Removed
import reactor.core.publisher.Flux;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.OAuthAuthorization;

@Service
@Slf4j
public class ReactiveTwitterService {

    private final TwitterConfig twitterConfig;
    // private final TwitterStatusThreshold twitterStatusThreshold; // Removed
    private final OAuthAuthorization oAuthAuthorization;


    public ReactiveTwitterService(TwitterConfig twitterConfig,
                                  // TwitterStatusThreshold twitterStatusThreshold, // Removed
                                  OAuthAuthorization oAuthAuthorization) {
        this.twitterConfig = twitterConfig;
        // this.twitterStatusThreshold = twitterStatusThreshold; // Removed
        this.oAuthAuthorization = oAuthAuthorization; // Injected from TwitterConfig's @Bean method
    }

    public Flux<Status> streamTweets() {
        // this.twitterStatusThreshold.reset(); // Removed

        return Flux.create(emitter -> {
            final int statusesCountThreshold = twitterConfig.getStream().getStatusesCountThreshold();
            final java.util.concurrent.atomic.AtomicLong currentStatusCount = new java.util.concurrent.atomic.AtomicLong(0);

            TwitterStream twitterStream = new TwitterStreamFactory(oAuthAuthorization.getConfiguration()).getInstance();

            StatusListener listener = new StatusListener() {
                @Override
                public void onStatus(Status status) {
                    log.debug("Received status: {}", status.getId());
                    emitter.next(status);

                    if (statusesCountThreshold > 0) { // Only apply threshold if it's configured (>0)
                        long count = currentStatusCount.incrementAndGet();
                        if (count >= statusesCountThreshold) {
                            log.info("Configured status count threshold ({}) reached. Completing stream.", statusesCountThreshold);
                            twitterStream.cleanUp(); // Clean up before completing emitter
                            twitterStream.shutdown();
                            emitter.complete();
                        }
                    }
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    log.info("Received deletion notice: {}", statusDeletionNotice);
                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                    log.warn("Received track limitation notice. Number of limited statuses: {}", numberOfLimitedStatuses);
                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {
                    log.info("Received scrub geo notice for userId {} upToStatusId {}", userId, upToStatusId);
                }

                @Override
                public void onStallWarning(StallWarning warning) {
                    log.warn("Received stall warning: {}", warning);
                    // Optionally, you could emit an error or a specific event for stall warnings
                    // emitter.error(new RuntimeException("Stall warning received: " + warning.getMessage()));
                }

                @Override
                public void onException(Exception ex) {
                    log.error("Exception on Twitter stream: ", ex);
                    emitter.error(ex);
                }
            };

            twitterStream.addListener(listener);

            // Apply filter
            String keywords = twitterConfig.getFilter().getKeywords();
            if (keywords == null || keywords.trim().isEmpty()) {
                log.warn("No filter keywords configured. Listening to sample stream. This might be rate limited quickly.");
                // twitterStream.sample(); // Listening to sample might be too much, let's ensure keywords exist or fail
                emitter.error(new IllegalStateException("Filter keywords are not configured in twitter.filter.keywords"));
                return; // Stop further processing if keywords are missing
            }

            log.info("Filtering stream with keywords: {}", keywords);
            FilterQuery filterQuery = new FilterQuery();
            filterQuery.track(keywords.split(",")); // Assuming keywords can be comma-separated

            twitterStream.filter(filterQuery);

            // Handle disposal: cleanup TwitterStream resources
            emitter.onDispose(() -> {
                log.info("Flux emitter disposed. Cleaning up and shutting down TwitterStream.");
                twitterStream.cleanUp();
                twitterStream.shutdown();
            });
        });
    }
}
