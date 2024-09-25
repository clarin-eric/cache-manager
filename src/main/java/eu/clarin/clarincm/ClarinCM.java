package eu.clarin.clarincm;

import org.mapdb.DB;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.List;

public class ClarinCM implements CacheManager {

    private DB db;
    @Override
    public Cache getCache(String name) {
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return List.of();
    }
}
