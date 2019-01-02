package org.twitter.streaming.app.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Comparator;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class TwitterMessage implements Serializable, Comparable<TwitterMessage> {
    private long id;
    private long dateCreated;
    private String text;
    private String author;

    @Override
    public int compareTo(TwitterMessage message) {
        return Comparator.
                comparingLong(TwitterMessage::getDateCreated).
                compare(this, message);
    }
}
