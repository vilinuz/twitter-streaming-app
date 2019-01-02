package org.twitter.streaming.app.repository;

import org.twitter.streaming.app.util.StatusMockCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JSON file repository test")
class TwitterStatusesJsonCreatorTest {
    private TwitterStatusesJsonCreator repository;

    private StatusMockCreator creator;

    private static final String EXPECTED_OUTPUT = "[\n" +
            "  {\n" +
            "    \"twitterDetails\": \"{\\n  \\\"user\\\": {\\n    \\\"id\\\": 1,\\n    \\\"dateCreated\\\": 1,\\n    \\\"name\\\": \\\"u1\\\",\\n    \\\"screenName\\\": \\\"user1\\\"\\n  },\\n  \\\"messages\\\": [\\n    {\\n      \\\"id\\\": 1,\\n      \\\"dateCreated\\\": 1,\\n      \\\"text\\\": \\\"text1\\\",\\n      \\\"author\\\": \\\"u1\\\"\\n    },\\n    {\\n      \\\"id\\\": 2,\\n      \\\"dateCreated\\\": 2,\\n      \\\"text\\\": \\\"text2\\\",\\n      \\\"author\\\": \\\"u1\\\"\\n    },\\n    {\\n      \\\"id\\\": 3,\\n      \\\"dateCreated\\\": 3,\\n      \\\"text\\\": \\\"text3\\\",\\n      \\\"author\\\": \\\"u1\\\"\\n    }\\n  ]\\n}\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"twitterDetails\": \"{\\n  \\\"user\\\": {\\n    \\\"id\\\": 2,\\n    \\\"dateCreated\\\": 2,\\n    \\\"name\\\": \\\"u2\\\",\\n    \\\"screenName\\\": \\\"user2\\\"\\n  },\\n  \\\"messages\\\": [\\n    {\\n      \\\"id\\\": 4,\\n      \\\"dateCreated\\\": 4,\\n      \\\"text\\\": \\\"text4\\\",\\n      \\\"author\\\": \\\"u2\\\"\\n    },\\n    {\\n      \\\"id\\\": 5,\\n      \\\"dateCreated\\\": 5,\\n      \\\"text\\\": \\\"text5\\\",\\n      \\\"author\\\": \\\"u2\\\"\\n    },\\n    {\\n      \\\"id\\\": 6,\\n      \\\"dateCreated\\\": 6,\\n      \\\"text\\\": \\\"text6\\\",\\n      \\\"author\\\": \\\"u2\\\"\\n    }\\n  ]\\n}\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"twitterDetails\": \"{\\n  \\\"user\\\": {\\n    \\\"id\\\": 3,\\n    \\\"dateCreated\\\": 3,\\n    \\\"name\\\": \\\"u3\\\",\\n    \\\"screenName\\\": \\\"user3\\\"\\n  },\\n  \\\"messages\\\": [\\n    {\\n      \\\"id\\\": 7,\\n      \\\"dateCreated\\\": 7,\\n      \\\"text\\\": \\\"text7\\\",\\n      \\\"author\\\": \\\"u3\\\"\\n    },\\n    {\\n      \\\"id\\\": 8,\\n      \\\"dateCreated\\\": 8,\\n      \\\"text\\\": \\\"text8\\\",\\n      \\\"author\\\": \\\"u3\\\"\\n    },\\n    {\\n      \\\"id\\\": 9,\\n      \\\"dateCreated\\\": 9,\\n      \\\"text\\\": \\\"text9\\\",\\n      \\\"author\\\": \\\"u3\\\"\\n    }\\n  ]\\n}\"\n" +
            "  }\n" +
            "]";

    @BeforeEach
    void setUp() {
        creator = StatusMockCreator.instance();
        repository = new TwitterStatusesJsonCreator();
    }

    @Test
    @EnabledOnJre({ JRE.JAVA_9, JRE.JAVA_10 })
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Testing JSON file repo with missing file")
    void testWithNonExistentFile() {
        assertThrows(Exception.class, () -> repository.toJson(creator.mockTwitterUserWithStatuses(), Files.newBufferedWriter(Paths.get("C:\\some_not_existent_path"))));

    }

    @Test
    @EnabledOnJre({ JRE.JAVA_9, JRE.JAVA_10 })
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Testing JSON file repo with success")
    void testSuccessfulExecution() throws IOException {
        StringWriter writer = new StringWriter();
        repository.toJson(creator.mockTwitterUserWithStatuses(), writer);
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }
}