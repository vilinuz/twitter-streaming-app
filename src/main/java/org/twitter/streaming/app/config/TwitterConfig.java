package org.twitter.streaming.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

import java.nio.file.Path;

@Configuration // For @Bean methods
@ConfigurationProperties(prefix = "twitter")
@Getter
@Setter
public class TwitterConfig {

    private App app;
    private Filter filter;
    private Stream stream;
    private Filesystem filesystem;
    private Oauth oauth;

    @Getter
    @Setter
    public static class App {
        private String appname;
    }

    @Getter
    @Setter
    public static class Filter {
        private String keywords;
    }

    @Getter
    @Setter
    public static class Stream {
        private int processDuration;
        private int statusesCountThreshold; // Matches twitter.stream.statuses.count.threshold
    }

    @Getter
    @Setter
    public static class Filesystem {
        private String outputFolder; // Changed to String to match application.properties (Path can be converted later)
    }

    @Getter
    @Setter
    public static class Oauth {
        private String consumerKey;
        private String consumerSecret;
        private String accessToken;
        private String accessTokenSecret;
    }

    @Bean
    public OAuthAuthorization getAuthorisationConfig() {
        // This method is part of the TwitterConfig class itself,
        // so it can directly access the 'oauth' field once populated by @ConfigurationProperties.
        if (this.oauth == null) {
            throw new IllegalStateException("OAuth properties not configured under 'twitter.oauth' or not yet populated.");
        }
        if (this.oauth.getConsumerKey() == null || this.oauth.getConsumerSecret() == null ||
            this.oauth.getAccessToken() == null || this.oauth.getAccessTokenSecret() == null) {
            throw new IllegalStateException("One or more OAuth properties (consumerKey, consumerSecret, accessToken, accessTokenSecret) are null.");
        }
        final twitter4j.conf.Configuration conf = new ConfigurationBuilder().setDebugEnabled(false)
                .setOAuthConsumerKey(this.oauth.getConsumerKey())
                .setOAuthConsumerSecret(this.oauth.getConsumerSecret())
                .setOAuthAccessToken(this.oauth.getAccessToken())
                .setOAuthAccessTokenSecret(this.oauth.getAccessTokenSecret())
                .build();
        return new OAuthAuthorization(conf);
    }

    // Example getter to access a nested property, if needed elsewhere (Lombok provides these)
    // public String getConsumerKey() {
    //    return oauth != null ? oauth.getConsumerKey() : null;
    // }
}
