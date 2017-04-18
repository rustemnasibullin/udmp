package org.mtt.api.redis;

import org.mtt.webapi.core.WAPIException;

public interface IRedis {
    
       // Strings cmd
    
       void doSetCmd() throws WAPIException;
       void doGetRangeSetCmd(String key, String start, String end) throws WAPIException;
       void doAppendCmd(String key, String value) throws WAPIException;
       void doIncrCmd(String key, Object value) throws WAPIException;
       void doIncrByCmd(String key, Object value, Object by ) throws WAPIException;
       
       // Hset cmd
    
    
    
    
}
