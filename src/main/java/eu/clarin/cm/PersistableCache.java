package eu.clarin.cm;

import org.mapdb.HTreeMap;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class PersistableCache implements Cache {

    private String name;
    private HTreeMap<Object, Cache.ValueWrapper> hTreeMap;

    public PersistableCache(String name, HTreeMap hTreeMap){

        this.name = name;
        this.hTreeMap = hTreeMap;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {

        return new ValueWrapper(){

            @Override
            public Object get() {

                return PersistableCache.this.hTreeMap.get(key);
            }
        };
    }

    @Override
    public <T> T get(Object key, Class<T> type) {

        return type.cast(PersistableCache.this.hTreeMap.get(key));
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {

        return (T) this.hTreeMap.putIfAbsent(key, new ValueWrapper() {
            @Override
            public Object get() {
                try {
                    return valueLoader.call();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).get();
    }

    @Override
    public void put(Object key, Object value) {

        this.hTreeMap.put(key, new ValueWrapper() {
            @Override
            public Object get() {
                return value;
            }
        });
    }

    @Override
    public void evict(Object key) {

        this.hTreeMap.remove(key);
    }

    @Override
    public void clear() {

        this.hTreeMap.clear();
    }
}
