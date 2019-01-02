package org.twitter.streaming.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.nio.file.Path;

@Component
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:twitterapp.properties")
public class TwitterConfig {

    @Value("${twitter.app.appname}")
    private String twitterAppname;

    @Value("${twitter.filter.keywords}")
    private String twitterFilterKeywords;

    @Value("${twitter.stream.process.duration}")
    private int processDuration;

    @Value("${twitter.stream.statuses.count.threshold}")
    private int statusesThreshold;

    @Value("${twitter.filesystem.output.folder}")
    private Path outputFolder;

    @Value("${twitter.oauth.consumerKey}")
    private String twitterConsumerKey;

    @Value("${twitter.oauth.consumerSecret}")
    private String twitterConsumerSecret;

    @Value("${twitter.oauth.accessToken}")
    private String twitterAccessToken;

    @Value("${twitter.oauth.accessTokenSecret}")
    private String twitterAccessSecret;

    public String getTwitterAppname() {
        return twitterAppname;
    }

    public String getTwitterFilterKeywords() {
        return twitterFilterKeywords;
    }

    public int getProcessDuration() {
        return processDuration;
    }

    public int getStatusesThreshold() {
        return statusesThreshold;
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public String getTwitterAccessSecret() {
        return twitterAccessSecret;
    }

    @Bean
    public OAuthAuthorization getAuthorisationConfig() {
        final Configuration conf = new ConfigurationBuilder().setDebugEnabled(false)
                .setOAuthConsumerKey(twitterConsumerKey)
                .setOAuthConsumerSecret(twitterConsumerSecret)
                .setOAuthAccessToken(twitterAccessToken)
                .setOAuthAccessTokenSecret(twitterAccessSecret)
                .build();
        return new OAuthAuthorization(conf);    }
}
