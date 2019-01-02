package org.twitter.streaming.app.transform;

import org.twitter.streaming.app.domain.TwitterMessage;
import org.twitter.streaming.app.domain.TwitterUser;
import org.twitter.streaming.app.util.StatusMockCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing Entity Transformers")
class EntityTransformersTest {

    private StatusMockCreator statusMockCreator;

    @BeforeEach
    void setUp() {
        statusMockCreator = StatusMockCreator.instance();
    }

    @Test
    @DisplayName("Testing User Transformer with null value")
    void testTransformUserWithNullValue() {
        assertThrows(NullPointerException.class, () -> EntityTransformers.transformUser(null));
    }

    @Test
    @DisplayName("Testing Status Transformer with null value")
    void testTransformStatusWithNullValue() {
        assertThrows(NullPointerException.class, () -> EntityTransformers.transformStatus(null));
    }

    @Test
    @DisplayName("Testing Statuses Transformer with null value")
    void testTransformStatusesWithNullValue() {
        assertThrows(NullPointerException.class, () -> EntityTransformers.transformStatuses(null));
    }

    @Test
    @DisplayName("Testing Statuses Transformer with empty collection")
    void testTransformStatusesWithEmptyCollection() {
        Set<TwitterMessage> result = EntityTransformers.transformStatuses(new TreeSet<>());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Testing Status Transformer with actual status")
    void testTransformStatus() {
        TwitterMessage result = EntityTransformers.transformStatus(statusMockCreator.mockStatus());
        assertNotNull(result);
        assertEquals(StatusMockCreator.EXPECTED_MESSAGE, result);
    }

    @Test
    @DisplayName("Testing Status Transformer with actual status")
    void testTransformUser() {
        TwitterUser result = EntityTransformers.transformUser(statusMockCreator.mockStatus().getUser());
        assertNotNull(result);
        assertEquals(StatusMockCreator.EXPECTED_USER, result);
    }
}
