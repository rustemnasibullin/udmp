package org.mtt.webapi.memcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
import org.apache.log4j.Logger;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.mtt.webapi.core.IMemCache;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.dom.SimpleXSmartObject;
import org.mtt.webapi.utils.XUtils;

/**
 *  IMemCache implementation based upom EhCache framework.
 *
 *  @author rnasibullin@mtt.ru
 */

public class SystemCache implements IMemCache {
    
    private Ehcache xCache = null;
    boolean flQueue = true;
    Logger log = Logger.getLogger (SystemCache.class);
    
    public SystemCache() {
           super();
    }

    public void setXCache(Ehcache xCache) {
           this.xCache = xCache;
           this.xCache.bootstrap();
           this.xCache.getCacheManager().getCacheManagerEventListener().notifyCacheAdded("ru.mtt.core.SystemCACHE");
    }

    public Ehcache getXCache() {
           return xCache;
    }
    
    
    public void init () {
    }

    
    public void stop () {
        
        try {
           xCache.removeAll();
           xCache.dispose();
        } catch (Throwable ee) {
            XUtils.info(ee, log);  
        }
    }

    @Override
    public synchronized <T extends Object> T findByKey(Object keyAttributeValue, Class<T> c) {
           T obj = null;
           
           String ks = keyAttributeValue.toString();
           Element el = xCache.get(ks);
           if (el != null) return (T) el.getObjectValue();
           return obj;
    }

    @Override
    public synchronized <T extends Object> List<T> findByAttribute(String keyAttribute, Object attributeValue, Class<T> c) {

        List<T> res = Collections.emptyList();

        List ls =  xCache.getKeys();
        for (Object o: ls) {
            
             String ks = (String)o;
             if (ks.startsWith(c.getName())) {
                 XSmartObject ob = (XSmartObject) xCache.get(o).getObjectValue();
                 try {
                 Object vs = ob.getFieldByName(keyAttribute);
                 if (vs != null && vs.equals(attributeValue)) {
                 
                 res.add((T)o);    
                                         
                 }
                 } catch (WAPIException ee) {
                  ee.printStackTrace();    
                 }
             }
            
            
        }
        return res;
    }

    @Override
    public synchronized <T extends Object> boolean insert(T o) {

        XSmartObject so = (XSmartObject) o;
        
        try {
        
        
        
        Object key = so.getFieldByName("ID");
        Element el = new Element(key, o);    
        xCache.put(el); 
        xCache.flush();
       
        } catch (WAPIException ee) {
          ee.printStackTrace();  
        }

        return true;
        
    }

    @Override
    public synchronized <T extends Object> boolean insertOrReplace(T o) {

        XSmartObject so = (XSmartObject) o;
        try {
        
        Object key = so.getFieldByName("ID");
        Element el = new Element(key, o);    
        
        if (xCache.isKeyInCache(key)) {
           xCache.remove(key);
           xCache.put(el);
        }  else {
           xCache.put(el);
        }
            
        xCache.flush();
        
        } catch (WAPIException ee) {
          ee.printStackTrace();  
        }

        return true;

    }

    @Override
    public synchronized <T extends Object> boolean replace(T o) {

        XSmartObject so = (XSmartObject) o;
        try {

        Object key = so.getFieldByName("ID");
        Element el = new Element(key, o);    
        xCache.put(el); 
        xCache.flush();
                
        } catch (WAPIException ee) {
          ee.printStackTrace();  
        }

        
        return true;
       
        
    }

    @Override
    public synchronized <T extends Object> boolean remove(T o) {
        
        XSmartObject so = (XSmartObject) o;
        try {

        Object key = so.getFieldByName("ID");
        xCache.remove(key);
        xCache.flush();
        
        } catch (WAPIException ee) {
          ee.printStackTrace();  
        }
        
        return true;
        
    }

    @Override
    public synchronized <T extends Object> boolean update(String keyAttribute, Object attributeValue, Map changeValues, Class<T> c) {
 
           List ls =  xCache.getKeys();
           for (Object o: ls) {
               
                String ks = (String)o;
                if (ks.startsWith(c.getName())) {
                    XSmartObject ob = (XSmartObject) xCache.get(o).getObjectValue();
                    try {
                    Object vs = ob.getFieldByName(keyAttribute);
                    if (vs != null && vs.equals(attributeValue)) {
                        
                    Set<Map.Entry> xls = changeValues.entrySet();    
                    Iterator<Map.Entry> xelement = xls.iterator(); 
                    while (xelement.hasNext()) {
                           Map.Entry xe = xelement.next();
                           String k = (String) xe.getKey();
                           Object v = xe.getValue();
                           ob.setFieldByName(k, v);
                    }
                    
                    this.replace(ob);
                            
                    }
                    } catch (WAPIException ee) {
                     ee.printStackTrace();    
                    }
                }
               
               
           }

        return true;

    }
    
    
    public synchronized <T> List<T> readAllByClass (Class<T> c)  {
        
           ArrayList<T> res = new ArrayList<T>();
           List  keydata = xCache.getKeys();
           log.info("Keys: "+ keydata +" : "+c);
  
           for (Object k: keydata) {

                Element e = xCache.get(k);
                log.info("Keys: "+ k+" : "+e);
                log.info(e.getObjectKey()+" = "+e.getObjectValue());
                Object o  = e.getObjectValue();
               
               
                if (o.getClass().equals (c)) {
                    res.add ((T)o);
                }
               
           }
           
           log.info(xCache.getName() + "   xCache: "  +  keydata);
           log.info("readAllByClass: "  +  res);
           return res;
    
    };

    public void setQueueMode (boolean flQueue) {

           this.flQueue = flQueue;

    }



    public static void simpleTest (String[] args) {
        
        
           ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/webapibeans.xml");
           context.start();
           SystemCache s  = (SystemCache)context.getBean("SystemCache"); 
           SimpleXSmartObject x0 = new SimpleXSmartObject("xid","");                              
           s.remove(x0);
           SimpleXSmartObject xs =  s.findByKey("xid", SimpleXSmartObject.class);
           System.out.println ("XS: "+xs);
           SimpleXSmartObject o = new SimpleXSmartObject("xid","Test1234565555");
           s.insertOrReplace(o);
           SimpleXSmartObject xs1 =  s.findByKey("xid", SimpleXSmartObject.class);
           System.out.println ("XS1: "+xs1);


           SimpleXSmartObject x = new SimpleXSmartObject("xid","NewTest1234565555");
           s.insertOrReplace(x);
           SimpleXSmartObject xs2 =  s.findByKey("xid", SimpleXSmartObject.class);
           System.out.println ("XS2: "+xs2);
           
         
           context.close();
        
    }


    public static void producer () {
        
           ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/webapibeans.xml");
           context.start();
           SystemCache s  = (SystemCache)context.getBean("SystemCache"); 
           long j = 0;

           for (;;) {
               
               try {  
                   
                 Thread.currentThread().sleep (5000);
                 SimpleXSmartObject o = new SimpleXSmartObject("pid",new Date());
                 s.insertOrReplace(o);
                   
                 SimpleXSmartObject x = s.findByKey("fid", SimpleXSmartObject.class);
                 System.out.println ("Consumer: "+x);  
                 SimpleXSmartObject w = s.findByKey("pid", SimpleXSmartObject.class);
                 System.out.println ("Producer: "+w);  
                     
               } catch (Throwable ee) {

                 ee.printStackTrace();    

               }
               
               if ((j++)>1000) break;
               
           };

           context.close();
        
    }


    public static void consumer () {
        
           ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/webapibeans.xml");
           context.start();
           SystemCache s  = (SystemCache)context.getBean("SystemCache"); 
           long j = 0;

           for (;;) {
               
               try {  
                   
                 Thread.currentThread().sleep (5000);
                 SimpleXSmartObject o = s.findByKey("pid", SimpleXSmartObject.class);
                 System.out.println ("Producer: "+o);  

                 SimpleXSmartObject x = new SimpleXSmartObject("fid",new Date());
                 SimpleXSmartObject g = s.findByKey("fid", SimpleXSmartObject.class);
                 System.out.println ("Consumer: "+g);  
                 s.insertOrReplace(x);


                     
               } catch (Throwable ee) {

                 ee.printStackTrace();    

               }
               
               if ((j++)>1000) break;
               
           };

           context.close();
        
    }



    public static void main (String[] args) {
        
           if (System.getProperty("MASTER") != null) {
               producer ();
           } else {
               consumer ();
           }
        
    }
    

}
