package leighedwards.assessment.ForgetMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.convert.DataSizeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ForgetMapTest {

    ForgetMap<String, String> forgetMap;

    @Test
    @DisplayName("Test that an element can be added and retrieved from map")
    public void confirmAddElement() {
        assert(false);
    }

    @Test
    @DisplayName("Test that the map will not exceed the max number of associations")
    public void confirmMaxAssociations() {
        assert(false);
    }

    @Test
    @DisplayName("Test that the map is successfuly removing lowest usage values")
    public void confirmRemovalOfLowestUsageValues() {
        assert(false);
    }

    @Test
    @DisplayName("Test that the map is removing a value in event of tie breaker")
    public void confirmRemovalWithTieBreaker() {
        assert(false);
    }

    @Test
    @DisplayName("Confirm usage count is correct after asynchronous calls to map")
    public void confirmAsynchronousUsageCount() {
        assert(false);
    }
}