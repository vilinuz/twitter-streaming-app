package org.twitter.streaming.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
/**
 * This class represents th threshold as configured in twitterapp.properties file.
 * See <code>{@link twitter.stream.statuses.count.threshold}</code> and <code>{@link twitter.stream.process.duration}</code> properties.
 */
public class TwitterStatusThreshold {
    private TwitterConfig config;
    private Instant startTime;
    private AtomicInteger counter;

    public TwitterStatusThreshold() {
        this.startTime = Instant.now();
        this.counter = new AtomicInteger(0);
    }

    @Autowired
    public void setConfig(TwitterConfig config) {
        this.config = config;
    }

    /**
     * Checks if the time elapsed as configured.
     * @return true if the time elapsed, else false
     */
    private boolean hasTimeElapsed() {
        return Duration.ofSeconds(config.getProcessDuration()).
                compareTo(Duration.between(startTime, Instant.now())) <= 0;
    }

    /**
     * Checks if maximum number of statuses has been reached.
     * @return true if reached, else false
     */
    private boolean reachedMaxStatusesCount() {
        return counter.get() > config.getStatusesThreshold();
    }

    /**
     * Checks some of the thresholds has been reached.
     * See <code>{@link TwitterStatusThreshold#reachedMaxStatusesCount()}</code> and <code>{@link TwitterStatusThreshold#hasTimeElapsed()}</code>.
     * @return true if some of the thresholds has been reached
     */
    public boolean reached() {
        return reachedMaxStatusesCount() || hasTimeElapsed();
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter.incrementAndGet();
    }

}
