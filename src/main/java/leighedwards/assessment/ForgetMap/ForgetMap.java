package leighedwards.assessment.ForgetMap;

import jdk.jshell.spi.ExecutionControl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ForgetMap<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ForgetMap.class.getName());

    private ConcurrentHashMap<K, ContentMap<V>> cache = new ConcurrentHashMap<>();
    private long maxAssociations;

    public ForgetMap(long maxAssociations) {
        this.maxAssociations = maxAssociations;
    }

    public synchronized void add(K k, V v) {
        if (cache.containsKey(k)) {
            LOGGER.info(String.format("Key: %s already found in ForgetMap, will override", k));
        }
    }

    public synchronized long size() {
        return cache.size();
    }

    public synchronized Set<K> keySet() {
        return cache.keySet();
    }

    public synchronized long usageByKey(K k) {
        return -1;
    }

    public V find(K key) {
        return null;
    }

    private class ContentMap<V> {
        private V contentValue;
        private LocalDateTime timeAdded;
        private long usageCount;

        public V getContentValue() {
            return contentValue;
        }

        public LocalDateTime getTimeAdded() {
            return timeAdded;
        }

        public long getUsageCount() {
            return usageCount;
        }

        public void incrementUsage(int usageCount) {
            // Thread-safe increment of content usage.
        }
    }
}
