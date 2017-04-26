package org.mtt.api.redis;

import java.io.CharArrayReader;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.mtt.webapi.core.WAPIException;

import redis.clients.jedis.Jedis; 
import java.util.List;
import java.util.Set;

import org.mtt.webapi.core.IMemCache;

public class RedisInstanceHandler implements IMemCache, IRedis{
    @Override
    public Object del(String key, Object properties) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void expire(String key, Object time) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void exec() throws WAPIException {
        // TODO Implement this method
    }

    public RedisInstanceHandler() {
        super();
    }


    @Override
    public void append(String key, String value) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void config(String property, Object[] parameters) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void eval(String script) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void evalsha(String sAlias, Object[] parameters) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public boolean exists(String sAlias) throws WAPIException {
        // TODO Implement this method
        return false;
    }

    @Override
    public void expireat(String key, Date time) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public <T extends Object> List<T> findByAttribute(String keyAttribute, Object attributeValue, Class<T> c) {
        // TODO Implement this method
        return Collections.emptyList();
    }

    @Override
    public <T extends Object> T findByKey(Object keyAttributeValue, Class<T> c) {
        // TODO Implement this method
        return null;
    }

    @Override
    public void flush(String sAlias) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public String get(String key) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void getrangeset(String key, String start, String end) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public Object getset(String key1, Object v) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public Map hget(String key, Object properties) throws WAPIException {
        // TODO Implement this method
        return Collections.emptyMap();
    }

    @Override
    public List hgetall(String key, Object properties) throws WAPIException {
        // TODO Implement this method
        return Collections.emptyList();
    }

    @Override
    public Set hkeys(String key) throws WAPIException {
        // TODO Implement this method
        return Collections.emptySet();
    }

    @Override
    public Map hmget(String key, Object properties) throws WAPIException {
        // TODO Implement this method
        return Collections.emptyMap();
    }

    @Override
    public void hmset(String key, Object properties) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void hscan() throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void hset(String key, Map value) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void incr(String key, Object value) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void incrby(String key, Object value, Object by) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public <T extends Object> boolean insert(T o) {
        // TODO Implement this method
        return false;
    }

    @Override
    public <T extends Object> boolean insertOrReplace(T o) {
        // TODO Implement this method
        return false;
    }

    @Override
    public Set keys(String keyPattern) throws WAPIException {
        // TODO Implement this method
        return Collections.emptySet();
    }

    @Override
    public void kill(String sAlias) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void lpush(String key, Object value) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void lrange(String key, int indx1, int indx2) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void monitor() {
        // TODO Implement this method
    }

    @Override
    public void multy() throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void persists(String key) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void publish(String channel, Object data) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public <T extends Object> List<T> readAllByClass(Class<T> c) {
        // TODO Implement this method
        return Collections.emptyList();
    }

    @Override
    public <T extends Object> boolean remove(T o) {
        // TODO Implement this method
        return false;
    }

    @Override
    public <T extends Object> boolean replace(T o) {
        // TODO Implement this method
        return false;
    }

    @Override
    public void rpush(String key, Object[] values) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void sadd(String key, Object properties) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void sadd(String key, Object[] data) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void scan() throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void script(String script, String sAlias) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void set() throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void setDefaultTTL(String key, Object t) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void setnx(String key1, Object v) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void setQueueMode(boolean flQueue) {
        // TODO Implement this method
    }

    @Override
    public void sinterstore(String key1, String key2, String newKey) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public Set sinter(String key1, String key2) throws WAPIException {
        // TODO Implement this method
        return Collections.emptySet();
    }

    @Override
    public Object setx(String key, Object t, String info) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void sismember(String key, Object data) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void sismember(String key, Object[] data) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public Object[] slowlog(String cmd) throws WAPIException {
        // TODO Implement this method
        return new Object[0];
    }

    @Override
    public Object[] slowlog(String cmd, Object[] parameters) throws WAPIException {
        // TODO Implement this method
        return new Object[0];
    }

    @Override
    public void sortby(String key, Object vkey) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void sort(String key) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void subscribe(String channel) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void sscan() throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void subscribe(String[] channel) throws WAPIException {
        // TODO Implement this method
    }

    @Override
    public void trim(Object key1, Object key2) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public Date ttl(String key) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public <T extends Object> boolean update(String keyAttribute, Object attributeValue, Map changeValues, Class<T> c) {
        // TODO Implement this method
        return false;
    }

    @Override
    public void zadd(String key, Object properties) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void zadd(String key, Object[] properties) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void zcount(String key, Object v1, Object v2) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void zrevrank(String key, Object v1) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public void zscan() throws WAPIException {
        // TODO Implement this method
    }

    public static void main1(String[] args) { 
         //Connecting to Redis server on localhost 
         Jedis jedis = new Jedis("localhost"); 
         System.out.println("Connection to server sucessfully"); 
         //store data in redis list 
         // Get the stored data and print it 
         Set<String> list = jedis.keys("*"); 
         
         for(String x: list) { 
            System.out.println("List of stored keys:: "+x); 
         } 
    }  
    
    public static void main2(String[] args) { 
         //Connecting to Redis server on localhost 
         Jedis jedis = new Jedis("localhost"); 
         System.out.println("Connection to server sucessfully"); 
         //store data in redis list 
         jedis.lpush("tutorial-list", "Redis"); 
         jedis.lpush("tutorial-list", "Mongodb"); 
         jedis.lpush("tutorial-list", "Mysql"); 
         // Get the stored data and print it 
         List<String> list = jedis.lrange("tutorial-list", 0 ,5); 
         for(int i = 0; i<list.size(); i++) { 
            System.out.println("Stored string in redis:: "+list.get(i)); 
         } 
    }     
    
    
    public static void main3(String[] args) { 
         //Connecting to Redis server on localhost 
         Jedis jedis = new Jedis("localhost"); 
         System.out.println("Connection to server sucessfully"); 
         //set the data in redis string 
         jedis.set("tutorial-name", "Redis tutorial"); 
         // Get the stored data and print it 
         System.out.println("Stored string in redis:: "+ jedis.get("tutorialname")); 
    } 
    
    public static void main12(String[] args) { 
       //Connecting to Redis server on localhost 
       Jedis jedis = new Jedis("172.16.102.99"); 
       System.out.println("Connection to server sucessfully"); 
       //check whether server is running or not 
       System.out.println("Server is running: "+jedis.ping()); 
    }     
   
    public static void main(String[] args) {
    }
    
}
