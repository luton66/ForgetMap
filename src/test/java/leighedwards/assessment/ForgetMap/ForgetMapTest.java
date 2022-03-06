package leighedwards.assessment.ForgetMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ForgetMapTest {

    ForgetMap<String, String> forgetMap;

    @Test
    @DisplayName("Test that an element can be added and retrieved from map")
    public void confirmAddElement() {
        forgetMap = new ForgetMap<String, String>(1);

        forgetMap.add("test1", "test1 value");

        assertEquals(forgetMap.find("test1"), "test1 value");
    }

    @Test
    @DisplayName("Test that the map will not exceed the max number of associations")
    public void confirmMaxAssociations() {
        forgetMap = new ForgetMap<String, String>(3);
        forgetMap.add("test1", "test value");
        forgetMap.add("test2", "test value");
        forgetMap.add("test3", "test value");
        forgetMap.add("test4", "test value");

        assertEquals(forgetMap.size(), 3);
    }

    @Test
    @DisplayName("Test that the map is successfuly removing lowest usage values")
    public void confirmRemovalOfLowestUsageValues() {
        //create map and add elements
        forgetMap = new ForgetMap<String, String>(3);
        forgetMap.add("test1", "test value");
        forgetMap.add("test2", "test value");
        forgetMap.add("test3", "test value");

        forgetMap.find("test1");
        forgetMap.find("test1");

        forgetMap.find("test2");
        forgetMap.find("test2");

        forgetMap.find("test3");

        forgetMap.add("test4", "test value");

        assertFalse(forgetMap.keySet().contains("test3"));
    }

    @Test
    @DisplayName("Test that the map is removing a value in event of tie breaker")
    public void confirmRemovalWithTieBreaker() {
        forgetMap = new ForgetMap<String, String>(2);
        forgetMap.add("test1", "test value");
        forgetMap.add("test2", "test value");

        forgetMap.find("test1");
        forgetMap.find("test1");

        forgetMap.find("test2");
        forgetMap.find("test2");

        forgetMap.add("test3", "test value");

        assertEquals(forgetMap.size(), 2);
    }

    @Test
    @DisplayName("Confirm usage count is correct after asynchronous calls to map")
    public void confirmAsynchronousUsageCount() {
        forgetMap = new ForgetMap<String, String>(2);
        forgetMap.add("test1", "test value");

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);

        // 100 calls to be made to find("test1") over 10 threads
        for (int i = 0; i < 100; i++) {
            service.submit(() -> {
                forgetMap.find("test1");
                // System.out.println(Thread.currentThread().getName());
            });
        }

        assertEquals(forgetMap.usageByKey("test1"), 100);
    }
}