package leighedwards.assessment.ForgetMap;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ForgetMap<K, V> {

    HashMap<K, ContentMap<V>> cache = new HashMap<>();

    public ForgetMap(int maxAssociations) {
    }

    public void add(V value) {
    }

    public V find(K key) {
        return null;
    }

    private class ContentMap<V> {
        private V contentValue;
        private LocalDateTime timeAdded;
        private int usageCount;

        public V getContentValue() {
            return contentValue;
        }

        public LocalDateTime getTimeAdded() {
            return timeAdded;
        }

        public int getUsageCount() {
            return usageCount;
        }

        public void incrementUsage(int usageCount) {
            // Thread-safe increment of content usage.
        }
    }
}
