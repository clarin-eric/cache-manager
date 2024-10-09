package eu.clarin.cm;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.mapdb.DB;

import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class PersistableCacheManager implements CacheManager {

    private static final Pattern patter = Pattern.compile("((\\S+)=)?(\\d+)([s|m|h|d]{1})");

    @Value("${spring.cache.cm.dbfile}")
    private String dbFile;

    @Value("${spring.cache.cm.expiration}")
    private String expiration;

    private DB db;

    private Long generalExpirationValue;
    private TimeUnit generalExpirationTimeUnit;

    @Override
    public Cache getCache(String name) {

        return new PersistableCache(name, this.db.hashMap(name).createOrOpen());
    }

    @Override
    public Collection<String> getCacheNames() {

        return StreamSupport.stream(this.db.getAllNames().spliterator(), false).toList();
    }

    @PostConstruct
    public void loadDB(){

        DBMaker.Maker dbMaker;

        if(this.dbFile != null){

            dbMaker = DBMaker.fileDB(this.dbFile);
        }
        else{

            dbMaker = DBMaker.memoryDB();
        }
        
        this.db = dbMaker.make();

        if(this.expiration != null){

            Matcher matcher = patter.matcher(this.expiration);

            while(matcher.find()){

                if(matcher.group(2) != null){ // this would be the specific cache name

                    this.db.hashSet(matcher.group(2))
                            .expireAfterCreate(Long.valueOf(matcher.group(3)), this.getTimeUnit(matcher.group(4))).create();

                }
                else{ // it's a general setting

                    this.generalExpirationValue = Long.valueOf(matcher.group(3));
                    this.generalExpirationTimeUnit = this.getTimeUnit(matcher.group(4));
                }
            }
        }
    }

    @PreDestroy
    public void persistDB(){

        if(this.dbFile != null ){

            this.db.commit();
            this.db.close();
        }
    }

    private TimeUnit getTimeUnit(String unit){

        return switch (unit){
            case "s" -> TimeUnit.SECONDS;
            case "m" -> TimeUnit.MINUTES;
            case "h" -> TimeUnit.HOURS;
            case "d" -> TimeUnit.DAYS;
            default -> null;
        };
    }
}
