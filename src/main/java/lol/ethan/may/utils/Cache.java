package lol.ethan.may.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new HashMap<>();
    public long TIME_TO_LIVE = 300000L;

    public Cache() {
    }

    public Cache(final long ttl) {
        TIME_TO_LIVE = ttl;
    }

    public boolean isExpired(final CacheEntry<V> entry) {
        return System.currentTimeMillis() > entry.time;
    }

    public V get(final K key) {
        final CacheEntry<V> entry = cache.get(key);
        if (null == entry || isExpired(entry)) {
            return null;
        }
        return entry.value;
    }

    public void put(final K key, final V value) {
        cache.put(key, new CacheEntry<>(value));
    }

    public boolean has(final K key) {
        final CacheEntry<V> entry = cache.get(key);
        return null != entry && !isExpired(entry);
    }

    public boolean hasValue(final V value) {
        for (final CacheEntry<V> entry : cache.values()) {
            if (null == entry || isExpired(entry) || !Objects.equals(entry.value, value)) continue;
            return true;
        }
        return false;
    }

    public static class CacheEntry<V> {
        public V value;
        public long time;

        public CacheEntry(final V value, final long time) {
            this.value = value;
            this.time = time;
        }

        public CacheEntry(final V value) {
            this.value = value;
            time = System.currentTimeMillis();
        }
    }
}

