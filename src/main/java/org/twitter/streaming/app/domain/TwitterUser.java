package org.twitter.streaming.app.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class TwitterUser implements Serializable, Comparable<TwitterUser> {

    private long id;
    private long dateCreated;
    private String name;
    private String screenName;
    private Set<TwitterMessage> messages;

    public void addMessages(final Collection<TwitterMessage> messages) {
        this.messages = new TreeSet<>();
        this.messages.addAll(messages);
    }

    @Override
    public int compareTo(TwitterUser user) {
        return Comparator.
                comparingLong(TwitterUser::getDateCreated).
                compare(this, user);
    }
}
