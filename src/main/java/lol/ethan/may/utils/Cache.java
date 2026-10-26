package lol.ethan.may.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new HashMap<>();
    public long TIME_TO_LIVE = 300000L;

    public Cache() {
    }

    public Cache(long ttl) {
        this.TIME_TO_LIVE = ttl;
    }

    public boolean isExpired(CacheEntry<V> entry) {
        return System.currentTimeMillis() > entry.time;
    }

    public V get(K key) {
        CacheEntry<V> entry = this.cache.get(key);
        if (entry == null || this.isExpired(entry)) {
            return null;
        }
        return entry.value;
    }

    public void put(K key, V value) {
        this.cache.put(key, new CacheEntry<>(value));
    }

    public boolean has(K key) {
        CacheEntry<V> entry = this.cache.get(key);
        return entry != null && !this.isExpired(entry);
    }

    public boolean hasValue(V value) {
        for (CacheEntry<V> entry : this.cache.values()) {
            if (entry == null || this.isExpired(entry) || !Objects.equals(entry.value, value)) continue;
            return true;
        }
        return false;
    }

    public static class CacheEntry<V> {
        public V value;
        public long time;

        public CacheEntry(V value, long time) {
            this.value = value;
            this.time = time;
        }

        public CacheEntry(V value) {
            this.value = value;
            this.time = System.currentTimeMillis();
        }
    }
}

