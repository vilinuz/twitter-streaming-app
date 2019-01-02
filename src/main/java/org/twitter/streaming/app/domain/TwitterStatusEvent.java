package org.twitter.streaming.app.domain;

import lombok.*;

import java.util.Set;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true, builderMethodName = "instance")
public class TwitterStatusEvent {
    private TwitterUser user;
    @Singular  private Set<TwitterMessage> messages;
}
