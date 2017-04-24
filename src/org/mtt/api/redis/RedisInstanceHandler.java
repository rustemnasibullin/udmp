package org.mtt.api.redis;

import java.io.CharArrayReader;

import java.io.IOException;

import redis.clients.jedis.Jedis; 
import java.util.List;
import java.util.Set;

import org.mtt.webapi.core.IMemCache;

public class RedisInstanceHandler implements IMemCache, IRedis{
    
    public RedisInstanceHandler() {
        super();
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
            String s = "abcdefgh";
            int stringLength = s.length();
            char chars[] = new char[stringLength];
            s.getChars(0, stringLength, chars, 0);
            CharArrayReader reader1 = newCharArrayReader(c);
            CharArrayReader reader2 = newCharArrayReader(c, 1, 4);
            int i;
            int j;
            try {
                while ((i = reader1.read()) == (j = reader2.read())) {
                    System.out.print((char) i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    
}
