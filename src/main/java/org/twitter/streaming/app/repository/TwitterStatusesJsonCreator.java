package org.twitter.streaming.app.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterUser;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TwitterStatusesJsonCreator {

    /**
     * Converts a map of Twitter users and their messages into a JSON string and writes it to the provided writer.
     *
     * @param collectedTweets A map where keys are TwitterUser objects and values are lists of TwitterMessage objects.
     * @param writer          The writer to output the JSON to.
     * @return The JSON string.
     * @throws IOException If an I/O error occurs.
     */
    public String toJson(final Map<TwitterUser, List<TwitterMessage>> collectedTweets, Writer writer) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
        JsonArray usersArray = new JsonArray();

        for (Map.Entry<TwitterUser, List<TwitterMessage>> entry : collectedTweets.entrySet()) {
            TwitterUser user = entry.getKey();
            List<TwitterMessage> messages = entry.getValue();

            JsonObject userJson = new JsonObject();
            // Serialize user details (e.g., screenName, id).
            // Assuming TwitterUser has appropriate fields and getters.
            // For this example, let's directly use gson.toJsonTree on the user object
            // if TwitterUser is a simple POJO suitable for direct serialization.
            // If not, specific fields should be added manually.
            userJson.add("user", gson.toJsonTree(user));

            JsonArray messagesArray = new JsonArray();
            for (TwitterMessage message : messages) {
                // Similarly, serialize message details.
                // Assuming TwitterMessage is suitable for direct serialization.
                messagesArray.add(gson.toJsonTree(message));
            }
            userJson.add("messages", messagesArray);
            usersArray.add(userJson);
        }

        String jsonOutput = gson.toJson(usersArray);
        log.debug("Generated JSON output: {}", jsonOutput); // Log actual JSON only if small/debug, otherwise log size or confirmation
        writer.write(jsonOutput);
        writer.flush();
        return jsonOutput; // Returning the json string might be useful for testing or other purposes
    }
}
