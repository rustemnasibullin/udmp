package org.mtt.api.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.Set;

import org.mtt.webapi.core.WAPIException;

public interface IRedis {
    
       // Strings cmd
    
       String get(String key) throws WAPIException;
       void set() throws WAPIException;
       void getrangeset(String key, String start, String end) throws WAPIException;
       void append(String key, String value) throws WAPIException;
       void incr(String key, Object value) throws WAPIException;
       void incrby(String key, Object value, Object by ) throws WAPIException;
       
       // Hashes cmd
    
       void hset(String key, Map value) throws WAPIException;
       Map hget(String key, Object properties) throws WAPIException;
       void hmset(String key, Object properties) throws WAPIException;
       Map hmget(String key, Object properties) throws WAPIException;
       List hgetall(String key, Object properties) throws WAPIException;
       Set hkeys(String key) throws WAPIException;
       Object del(String key, Object properties) throws WAPIException;
       
    
    
       // Lists cmd
    
       void lpush(String key, Object value) throws WAPIException; 
       void trim(Object key1, Object key2) throws WAPIException; 
       void lrange(String key, int indx1, int indx2) throws WAPIException; 

       // Sets cmd

       void sadd(String key, Object properties) throws WAPIException; 
       void sadd(String key, Object[] data) throws WAPIException; 

       void sismember(String key, Object data) throws WAPIException; 
       void sismember(String key, Object[] data) throws WAPIException; 
       Set sinter(String key1, String key2) throws WAPIException; 
       void sinterstore(String key1, String key2, String newKey) throws WAPIException; 

       // Sorted set cmd
       void zadd(String key, Object properties) throws WAPIException; 
       void zadd(String key, Object[] properties) throws WAPIException; 
       void zcount(String key, Object v1, Object v2) throws WAPIException; 
       void zrevrank(String key, Object v1) throws WAPIException; 


       // transaction management 
       
       Object getset (String key1, Object v) throws WAPIException; 
       void setnx (String key1, Object v) throws WAPIException; 
       void multy () throws WAPIException; 
       void exec () throws WAPIException; 
       
       Set keys (String keyPattern) throws WAPIException;
       
       // expiration management
       void expire (String key, Object time) throws WAPIException;
       void expireat (String key, Date time) throws WAPIException;
 
       Date ttl (String key) throws WAPIException;
       void persists (String key) throws WAPIException;
       void setDefaultTTL (String key, Object t) throws WAPIException;
       Object setx (String key, Object t, String info) throws WAPIException;
 
       // publication and Subscription management
       void subscribe (String channel) throws WAPIException;
       void subscribe (String[] channel) throws WAPIException;
       void publish (String channel, Object data) throws WAPIException;
       
       // monitor
       void monitor();
       void config(String property, Object[] parameters) throws WAPIException;
       Object[] slowlog(String cmd) throws WAPIException;
       Object[] slowlog(String cmd, Object[] parameters) throws WAPIException;
       
       // sort
       
       void rpush (String key, Object[] values) throws WAPIException;
       void sort (String key) throws WAPIException;
       void sortby (String key, Object vkey) throws WAPIException;
       
       // scan
        
       void scan() throws WAPIException;
       void hscan() throws WAPIException;
       void sscan() throws WAPIException;
       void zscan() throws WAPIException;
       
       // LUA scripting
       
       void eval (String script) throws WAPIException;
       void script (String script, String sAlias) throws WAPIException;
       void evalsha (String sAlias, Object[] parameters) throws WAPIException;
       void kill (String sAlias) throws WAPIException;
       void flush (String sAlias) throws WAPIException;
       boolean exists (String sAlias) throws WAPIException;

       
    
}
