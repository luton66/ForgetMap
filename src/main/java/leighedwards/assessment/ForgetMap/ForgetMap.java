package leighedwards.assessment.ForgetMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ForgetMap<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ForgetMap.class.getName());

    private ConcurrentHashMap<K, ContentMap<V>> cache = new ConcurrentHashMap<>();
    private long maxAssociations;

    public ForgetMap(long maxAssociations) {
        this.maxAssociations = maxAssociations;
    }

    public synchronized void add(K k, V v) {
        ContentMap<V> content = new ContentMap<V>(v);

        if (cache.containsKey(k)) {
            LOGGER.info(String.format("Key: %s already found in ForgetMap, will override", k));
        }
        else if (cache.size() >= maxAssociations) {
            removeLeastUsedValue();
        }

        cache.put(k, content);
    }

    public synchronized V find(K k) {
        ContentMap<V> contentMap = cache.get(k);

        if (contentMap == null) {
            LOGGER.info(String.format("No element found for Key: %s", k));
        }
        else {
            contentMap.incrementUsage();
            return contentMap.getContentValue();
        }

        return null;
    }

    public long size() {
        return cache.size();
    }

    public synchronized Set<K> keySet() {
        return cache.keySet();
    }

    public synchronized long usageByKey(K k) {
        ContentMap<V> contentMap = cache.get(k);

        if (contentMap == null) {
            LOGGER.info(String.format("No element found for Key: %s", k));
            return -1;
        }
        else {
            return contentMap.getUsageCount().get();
        }
    }

    private synchronized void removeLeastUsedValue() {
        AtomicLong lowestUsage = new AtomicLong(Long.MAX_VALUE);
        final ArrayList<K> lowestUseKeys = new ArrayList<>();

        while (cache.size() >= maxAssociations) {
            cache.keySet().forEach(key -> {
                ContentMap<V> contentMap = cache.get(key);
                if (contentMap.getUsageCount().get() == lowestUsage.get()) {
                    lowestUseKeys.add(key);
                } else if (contentMap.getUsageCount().get() < lowestUsage.get()) {
                    lowestUseKeys.clear();
                    lowestUsage.set(contentMap.getUsageCount().get());
                    lowestUseKeys.add(key);
                }
            });

            if (lowestUseKeys.size() > 1) {
                implementTieBreaker(lowestUseKeys);
            }
            else {
                cache.remove(lowestUseKeys.get(0));
            }
        }
    }

    private void implementTieBreaker(ArrayList<K> lowestUseKeys) {
        AtomicReference<LocalDateTime> oldestElement = new AtomicReference<>(LocalDateTime.now());
        AtomicReference<K> keyToRemove = new AtomicReference<>(lowestUseKeys.get(0));
        if (lowestUseKeys.size() == 1) {
            cache.remove(lowestUseKeys.get(0));
        }
        else {
            lowestUseKeys.forEach(key -> {
                if (cache.get(key).getTimeAdded().isBefore(oldestElement.get())) {
                    keyToRemove.set(key);
                    oldestElement.set(cache.get(key).getTimeAdded());
                }
            });

            cache.remove(keyToRemove.get());
        }
    }

    private class ContentMap<V> {
        private V contentValue;
        private LocalDateTime timeAdded;
        private AtomicLong usageCount;

        ContentMap(V v) {
            this.timeAdded = LocalDateTime.now();
            this.contentValue = v;
            this.usageCount = new AtomicLong();
        }

        V getContentValue() {
            return contentValue;
        }

        LocalDateTime getTimeAdded() {
            return timeAdded;
        }

        AtomicLong getUsageCount() {
            return usageCount;
        }

        void incrementUsage() {
            usageCount.incrementAndGet();
        }
    }
}
