package org.twitter.streaming.app.util;

import org.twitter.streaming.app.config.TwitterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterStatusCreator {

    private static final Random RANDOM = new Random();

    private Twitter twitter;
    private List<String> quotes;
    @Autowired
    private TwitterConfig config;

    private TwitterStatusCreator() throws URISyntaxException {
        Authorization authorization = config.getAuthorisationConfig();
        twitter = new TwitterFactory().getInstance(authorization);
        quotes = getQuotes();
    }

    private List<String> getQuotes() throws URISyntaxException {
        List<String> list = null;
        Class clazz = TwitterStatusCreator.class;
        URI uri = clazz.getResource("/quotes.list").toURI();
        try (Stream<String> lines = Files.lines(Paths.get(uri))) {
            list = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void generateStatuses() throws InterruptedException {
        while (true) {
            try {
                Thread.sleep(randomInterval());
                String quote = randomQuote();
                twitter.updateStatus(quote);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    int randomInterval() {
        return (RANDOM.nextInt(3) + 1) * 1000;
    }

    String randomQuote() {
        return quotes.get(RANDOM.nextInt(quotes.size()));
    }

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        TwitterStatusCreator creator = new TwitterStatusCreator();
        creator.generateStatuses();
    }
}
