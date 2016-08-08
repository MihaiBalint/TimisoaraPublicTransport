package ro.mihai.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mihai Balint on 8/9/16.
 */
public class NetDataCache<K, V> {
    public static long MAXAGE = 15000L;

    public interface OnMiss<K, V>  {
        V get(K key) throws IOException;
    }

    private class CacheData {
        public long timestamp;
        public V data;

        CacheData(V data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean invalid() {
            long age = System.currentTimeMillis() - this.timestamp;
            return age > MAXAGE;
        }
    }

    private HashMap<K, CacheData> cache = new HashMap<K, CacheData>();

    private void sleep() {
        // This is absolute evil!
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            // I don't care
        }
    }

    public V get(K key, OnMiss<K, V> onMiss) throws IOException {
        CacheData d = cache.get(key);
        if (d == null || d.invalid()) {
            evict();
            d = new CacheData(onMiss.get(key));
            cache.put(key, d);
        } else {
            this.sleep();
        }
        return d.data;
    }

    public void evict() {
        if (this.cache.size() <= 5)
            return;
        cache.remove(cache.keySet().iterator().next());
    }
}
