package leighedwards.assessment.ForgetMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * A 'forgetting map' designed to store a pre-defined number of associations, and then remove the least used
 * association.
 *
 * @param <K> a key to store against the provided content
 * @param <V> a value representing the associated content
 */
public class ForgetMap<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ForgetMap.class.getName());

    private ConcurrentHashMap<K, ContentMap<V>> cache = new ConcurrentHashMap<>();
    private long maxAssociations;

    /**
     * Constructor that will create a new ForgetMap that will establish the maximum number of associations the map
     * can hold.
     *
     * @param maxAssociations the maximum number of key-content associations that can be held by the ForgetMap
     */
    public ForgetMap(long maxAssociations) {
        this.maxAssociations = maxAssociations;
    }

    /**
     * Method to take a given key and content and store them in a local Hashmap. Synchronized to prevent multiple
     * threads attempting to update with the same key at the same time.
     *
     * @param k a key to store the provided content against
     * @param v the value which will be provided content
     */
    public synchronized void add(final K k, final V v) {
        ContentMap<V> content = new ContentMap<V>(v);

        if (cache.containsKey(k)) {
            LOGGER.info(String.format("Key: %s already found in ForgetMap, will override", k));
        }
        else if (cache.size() >= maxAssociations) {
            removeLeastUsedValue();
        }

        cache.put(k, content);
    }

    /**
     * Uses the provided key to retrieve the associated content from the local hashmap.
     *
     * @param k a provided key
     * @return any content stored in the local hashmap against the provided key
     */
    public V find(final K k) {
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

    /**
     * Returns the number of element in the ForgetMap.
     *
      * @return the number of elements in the ForgetMap
     */
    public long size() {
        return cache.size();
    }

    /**
     * Returns the ForgetMap keyset
     *
     * @return a Set of keys
     */
    public Set<K> keySet() {
        return cache.keySet();
    }

    /**
     * Checks against the content stored against a provided key and retrieves the number of times that association
     * has been retrieved using the found method.
     *
     * @param k a provided Key
     * @return the number of times content associated with provided key has bene retrieved using the find method
     */
    public long usageByKey(final K k) {
        ContentMap<V> contentMap = cache.get(k);

        if (contentMap == null) {
            LOGGER.info(String.format("No element found for Key: %s", k));
            return -1;
        }
        else {
            return contentMap.getUsageCount().get();
        }
    }

    /**
     * Checks against all values in the ForgetMap to find the least use element and remove it from the cache.
     */
    private synchronized void removeLeastUsedValue() {
        AtomicLong lowestUsage = new AtomicLong(Long.MAX_VALUE);
        final ArrayList<K> lowestUseKeys = new ArrayList<>();

        // establishes all elements with least-used associations
        while (cache.size() >= maxAssociations) {
            cache.forEach((key, contentMap) -> {
                if (contentMap.getUsageCount().get() == lowestUsage.get()) {
                    lowestUseKeys.add(key);
                }
                else if (contentMap.getUsageCount().get() < lowestUsage.get()) {
                    lowestUseKeys.clear();
                    lowestUsage.set(contentMap.getUsageCount().get());
                    lowestUseKeys.add(key);
                }
            });

            // if only one element is found, the element is removed. Otherwise the tie breaker element is used.
            if (lowestUseKeys.size() > 1) {
                implementTieBreaker(lowestUseKeys);
            }
            else {
                cache.remove(lowestUseKeys.get(0));
            }
        }
    }

    /**
     * Takes a List of keys, established to be the least-used, and then removes the element with the oldest timestamp.
     * If two elements were to have the exact same time-created, then the first one leaded will be removed.
     *
     * @param lowestUseKeys an ArrayList of keys
     */
    private synchronized void implementTieBreaker(final ArrayList<K> lowestUseKeys) {
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

    /**
     * Inner class used for storing the following elements...
     *  - the content provided by the user to be stored against the provided key in the ForgetMap
     *  - a date recording when the element was created in the ForgetMap.
     *  - an AtomicLong to store a thread-safe value representing the number of times each element has been retrieved
     *
     * @param <V> Generic to represent the type of data the user has requested to be stored in the ForgetMap
     */
    private class ContentMap<V> {
        private V contentValue;
        private LocalDateTime timeAdded;
        private AtomicLong usageCount;

        /**
         * Constructor.
         *
         * @param v the provided 'content' to be stored against the provided key in the ForgetMap
         */
        ContentMap(final V v) {
            this.timeAdded = LocalDateTime.now();
            this.contentValue = v;
            this.usageCount = new AtomicLong();
        }

        /**
         * Retrieves the content value for the ForgetMap.
         *
         * @return content
         */
        V getContentValue() {
            return contentValue;
        }

        /**
         * Retrieves the time the content was created.
         *
         * @return LocalDateTime representing when content was created
         */
        LocalDateTime getTimeAdded() {
            return timeAdded;
        }

        /**
         * Retrieves a value representing the number of times content was requested.
         *
         * @return an AtomicLong
         */
        AtomicLong getUsageCount() {
            return usageCount;
        }

        /**
         * Increase the value stored in usage count, representing the number of times the content has been retrieved.
         */
        void incrementUsage() {
            usageCount.incrementAndGet();
        }
    }
}
