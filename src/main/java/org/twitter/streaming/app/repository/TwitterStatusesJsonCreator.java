package org.twitter.streaming.app.repository;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterStatusEvent;
import org.twitter.streaming.app.domain.TwitterUser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

@Component
@Slf4j
public class TwitterStatusesJsonCreator {

    public String toJson(final Multimap<TwitterUser, TwitterMessage> statuses, Writer writer) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
        JsonArray array = new JsonArray();
        for (TwitterUser user : statuses.keySet()) {
            // Recreate Twitter Statuses Event with generated messages per user
            TwitterStatusEvent twitterStatusEvent = TwitterStatusEvent.instance().
                    user(user).
                    messages(statuses.get(user)).
                    build();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("twitterDetails", gson.toJson(twitterStatusEvent));
            array.add(jsonObject);
        }

        String json = gson.toJson(array);
        log.info(json);
        writer.write(json);
        writer.flush();
        return writer.toString();
    }
}
