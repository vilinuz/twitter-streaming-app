package org.twitter.streaming.app.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.twitter.streaming.app.domain.TwitterUser;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import twitter4j.Status;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

public class StatusMockCreator {
    public static final TwitterMessage EXPECTED_MESSAGE = TwitterMessage.builder().id(2L).dateCreated(111112).author("testUser1").text("bieber 1").build();
    public static final TwitterUser EXPECTED_USER = TwitterUser.builder().id(2).dateCreated(111112).name("testUser1").screenName("Test User 1").build();

    @Mock
    private Status status;

    @Mock
    private User user;

    public static StatusMockCreator instance() {
        return new StatusMockCreator();
    }

    public Status mockStatus() {
        return mockStatus("1", 1);
    }

    public Status mockStatus(String suffix, int suffixNumber) {
        MockitoAnnotations.initMocks(this);
        when(user.getName()).thenReturn("testUser" + suffix);
        when(user.getScreenName()).thenReturn("Test User " + suffix);
        when(user.getCreatedAt()).thenReturn(new Date(111111 + suffixNumber));
        when(user.getId()).thenReturn(1L + suffixNumber);
        when(user.getStatus()).thenReturn(status);
        when(status.getId()).thenReturn(1L + suffixNumber);
        when(status.getUser()).thenReturn(user);
        when(status.getText()).thenReturn("bieber " + suffix);
        when(status.getCreatedAt()).thenReturn(new Date(111111 + suffixNumber));
        return status;
    }

    public List<Status> mockStatuses(int numberOfStatuses) {
        List<Status> statuses = new ArrayList<>();
        for (int i = 0; i < numberOfStatuses; i++) {
            statuses.add(mockStatus(String.valueOf(i), i));
        }
        return statuses;
    }

    public TwitterStatusEvent mockTwitterEvent() {
        TwitterUser user1 = TwitterUser.builder().id(1).dateCreated(1).name("u1").screenName("user1").build();
        TwitterMessage message1 = TwitterMessage.builder().author("u1").id(1).dateCreated(1).text("justin bieber").build();
        return TwitterStatusEvent.instance().user(user1).messages(Sets.newTreeSet(Arrays.asList(message1))).build();
    }

    public Multimap<TwitterUser, TwitterMessage> mockTwitterUserWithStatuses() {
        TwitterUser user1 = TwitterUser.builder().id(1).dateCreated(1).name("u1").screenName("user1").build();
        TwitterUser user2 = TwitterUser.builder().id(2).dateCreated(2).name("u2").screenName("user2").build();
        TwitterUser user3 = TwitterUser.builder().id(3).dateCreated(3).name("u3").screenName("user3").build();
        TwitterMessage message1 = TwitterMessage.builder().author("u1").id(1).dateCreated(1).text("text1").build();
        TwitterMessage message2 = TwitterMessage.builder().author("u1").id(2).dateCreated(2).text("text2").build();
        TwitterMessage message3 = TwitterMessage.builder().author("u1").id(3).dateCreated(3).text("text3").build();
        TwitterMessage message4 = TwitterMessage.builder().author("u2").id(4).dateCreated(4).text("text4").build();
        TwitterMessage message5 = TwitterMessage.builder().author("u2").id(5).dateCreated(5).text("text5").build();
        TwitterMessage message6 = TwitterMessage.builder().author("u2").id(6).dateCreated(6).text("text6").build();
        TwitterMessage message7 = TwitterMessage.builder().author("u3").id(7).dateCreated(7).text("text7").build();
        TwitterMessage message8 = TwitterMessage.builder().author("u3").id(8).dateCreated(8).text("text8").build();
        TwitterMessage message9 = TwitterMessage.builder().author("u3").id(9).dateCreated(9).text("text9").build();
        Multimap<TwitterUser, TwitterMessage> usersWithStatuses = TreeMultimap.create();
        usersWithStatuses.put(user1, message1);
        usersWithStatuses.put(user1, message2);
        usersWithStatuses.put(user1, message3);
        usersWithStatuses.put(user2, message4);
        usersWithStatuses.put(user2, message5);
        usersWithStatuses.put(user2, message6);
        usersWithStatuses.put(user3, message7);
        usersWithStatuses.put(user3, message8);
        usersWithStatuses.put(user3, message9);
        return usersWithStatuses;
    }
}
