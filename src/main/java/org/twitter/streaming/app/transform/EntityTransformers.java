package org.twitter.streaming.app.transform;

import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterUser;
import twitter4j.Status;
import twitter4j.User;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to transform
 */
public class EntityTransformers {
    public static TwitterUser transformUser(final User user) {
        Objects.nonNull(user);
        return TwitterUser.builder().
                id(user.getId()).
                dateCreated(user.getCreatedAt().getTime()).
                name(user.getName()).
                screenName(user.getScreenName()).
                build();
    }

    static Set<TwitterMessage> transformStatuses(Set<Status> statuses) {
        Set<TwitterMessage> messages = new TreeSet<>(Comparator.comparing(TwitterMessage::getDateCreated));

        statuses.forEach( status -> {
            TwitterMessage twitterMessage = TwitterMessage.builder().
                    id(status.getId()).
                    dateCreated(status.getCreatedAt().getTime()).
                    author(status.getUser().getName()).
                    text(status.getText()).
                    build();
            messages.add(twitterMessage);
        });
        return messages;
    }

    public static TwitterMessage transformStatus(Status status) {
        return TwitterMessage.builder().
                id(status.getId()).
                dateCreated(status.getCreatedAt().getTime()).
                author(status.getUser().getName()).
                text(status.getText()).
                build();
    }
}
