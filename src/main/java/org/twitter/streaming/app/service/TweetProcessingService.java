package org.twitter.streaming.app.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.twitter.streaming.app.config.TwitterConfig;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.twitter.streaming.app.domain.TwitterUser;
import org.twitter.streaming.app.repository.TwitterStatusesJsonCreator;
import org.twitter.streaming.app.transform.EntityTransformers;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileWriter;
import java.io.IOException;
// import java.io.StringWriter; // No longer needed for placeholder
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TweetProcessingService {

    private final ReactiveTwitterService reactiveTwitterService;
    private final TwitterStatusesJsonCreator jsonCreator;
    private final TwitterConfig twitterConfig;

    public TweetProcessingService(ReactiveTwitterService reactiveTwitterService,
                                  TwitterStatusesJsonCreator jsonCreator,
                                  TwitterConfig twitterConfig) {
        this.reactiveTwitterService = reactiveTwitterService;
        this.jsonCreator = jsonCreator;
        this.twitterConfig = twitterConfig;
    }

    @PostConstruct
    public void processAndStoreTweets() {
        log.info("Starting tweet processing and storage pipeline...");

        reactiveTwitterService.streamTweets()
                .map(status -> {
                    if (status.getUser() == null) {
                        log.warn("Received status with null user: {}", status.getId());
                        return null;
                    }
                    return TwitterStatusEvent.instance()
                            .user(EntityTransformers.transformUser(status.getUser()))
                            .message(EntityTransformers.transformStatus(status))
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        TwitterStatusEvent::getUser,
                        Collectors.mapping(event -> {
                            if (event.getMessages() == null || event.getMessages().isEmpty()) {
                                return null;
                            }
                            // Filter out null messages just in case transformStatus could produce them
                            // (though it shouldn't based on its current logic)
                            return event.getMessages().stream().filter(Objects::nonNull).findFirst().orElse(null);
                        }, Collectors.filtering(Objects::nonNull, Collectors.toList())) // Ensure only non-null messages are in the list
                ))
                .flatMap(collectedTweets ->
                    Mono.fromRunnable(() -> persistCollectedTweets(collectedTweets))
                            .subscribeOn(Schedulers.boundedElastic())
                )
                .doOnError(error -> log.error("Error processing tweet stream: {}", error.getMessage(), error))
                .subscribe(
                        success -> log.info("Tweet processing pipeline completed and data persistence initiated."),
                        error -> log.error("Tweet processing pipeline terminated with an error: {}", error.getMessage(), error)
                );
    }

    private void persistCollectedTweets(Map<TwitterUser, List<TwitterMessage>> collectedTweets) {
        if (collectedTweets.isEmpty()) {
            log.info("No tweets collected to persist.");
            return;
        }
        log.info("Persisting {} users with their collected tweets.", collectedTweets.size());

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "tweets_" + timestamp + ".json";
        Path outputFolderPath = Paths.get(twitterConfig.getFilesystem().getOutputFolder());

        try {
            if (Files.notExists(outputFolderPath)) {
                Files.createDirectories(outputFolderPath);
                log.info("Created output directory: {}", outputFolderPath);
            }
            Path outputFilePath = outputFolderPath.resolve(filename);

            try (Writer writer = new FileWriter(outputFilePath.toFile())) {
                log.info("Attempting to write collected tweets to: {} using refactored TwitterStatusesJsonCreator", outputFilePath);
                jsonCreator.toJson(collectedTweets, writer); // Use the refactored method
                log.info("Successfully wrote JSON to {}", outputFilePath);
            } catch (IOException e) {
                log.error("Failed to write tweets to file {}: {}", outputFilePath, e.getMessage(), e);
            }
        } catch (IOException e) {
            log.error("Failed to create output directory {}: {}", outputFolderPath, e.getMessage(), e);
        }
    }
}
