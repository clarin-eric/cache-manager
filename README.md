# Cache Manger
## Project description
The goal of the project is to create a persistable cache manager for spring boot projects. The cache manager is integrated
in a spring boot project as a maven dependency and the cache manager is configured in the spring boot specific way
(see https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-external-config.html)

## Requirements for the cache manager
- usable as a maven dependency
- configurable as spring boot property
- loading its last state from the file system at first usage and persists the latest state into the file system after the
  last usage
- has configurable general expiration period of time and individual expiration period of time per cache

## Usage of the cache manager
### Maven dependency

### Configuration
````
spring.cache.cm.dbfile=<name of the file to persist to cache to. The cache is not persisted, if not set!>
spring.cache.cm.expiration=<comma separated list of cache settings. No expiration at runtime, if not set>
````
A general cache setting is following the pattern:
````
\d+[s|m|h|d]
````

A cache specific pattern is following the pattern:
````
.+=\d+[s|m|h|d]
````

Both setting, general and specific, can be combined in a comma separated list

Example
````
spring.cache.cm.dbfile=/tmp/mycache.db
spring.cache.cm.expiration=1d,cache1=12h,cache2=3h
````

The setting persists the cache in a file /tmp/mycache.db, which means when a program is re-run it's loading the 
cache from this location. Further on the general expiration time of each cache is 1 day, while for cache1 it is 
12 hours and for cache2 it is 3 hours