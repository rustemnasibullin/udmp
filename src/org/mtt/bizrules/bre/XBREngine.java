package org.mtt.bizrules.bre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import java.nio.CharBuffer;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.apache.commons.io.output.FileWriterWithEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.mtt.webapi.xresource.IXResource;
import org.mtt.webapi.core.IConstants;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.dom.SimpleXSmartObject;
import org.mtt.webapi.utils.XUtils;

public abstract class XBREngine  implements ApplicationContextAware, IBREEngine, IConstants {
    
    protected ScriptEnginePool engine = new ScriptEnginePool();
    AtomicInteger sessIdCounter  = new AtomicInteger (0);
    Map<Integer, Map<String, XSmartObject>> wMemoryExtended = new ConcurrentSkipListMap<Integer, Map<String, XSmartObject>>();
    Map<String, XSmartObject> wMemoryGlobal = new TreeMap<String, XSmartObject> ();
    private ApplicationContext applicationContext;
    private IXResource xresource;
    private String group;
    private static final Logger logger = LoggerFactory.getLogger(GroovyContextBuilder.class);
    abstract public String[] getResources();
    volatile boolean built  = false;
    public static final int DEFAULT_BUFFER_SIZE = 5000;
    FileWriter writer = null; 
    Map <String, String>  mps = new ConcurrentHashMap <String, String>();


    @Override
    public void setScriptEngineFactory(ScriptEngineFactory se) {
           engine.factory = se;
    }

    @Override
    public ScriptEngine getScriptEngine() {
           return  engine.getScriptEngine();
    }

    @Override
    public ScriptEngine getScriptEngine(int sessId) {
           return engine.enginesOnWork.get(sessId);
    }

    public synchronized void load (String path) {
        
           String [] paths = this.getResources();
        XUtils.ilog("log/XBREngine.log","BRE: "+path+"  -  "+this.getAlias());
           if (path.startsWith(this.getAlias().toLowerCase())) {
           for (String pi: paths) {
               
                if (path.startsWith(pi+".") || path.equals(pi)) {
                    String res = xresource.getResourceByPath(path);
                    XUtils.plog("log/XBREngine.log", res+"\n\n\n");
                    mps.put (path,res);
                    built = false; 
                }
               
           }
           }
          

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void init() {
        
        if (engine.factory != null) {
            if (!engine.isInitilized()) {
                engine.init();
            }
            
            if (!built && mps != null && mps.size()>0) {
                engine.upgradeWithRulesBANK(mps); 
                built = true;
            }
            
        }
       
    }


    /**
     *
     */
    public  void build()  {
        
        String [] paths = this.getResources();
       
        if (paths != null) {
            
            for (String p: paths) {

                XUtils.ilog("log/XBREngine.log","ReadPath  BRE: "+p);
                 Map <String, String>  mpso = xresource.getFolderByPathAsMAP(p);
                XUtils.ilog("log/XBREngine.log",p+"  BRE: "+mpso);
                
                 if (mpso != null) {
                     mps.putAll(mpso);
                 }
            }
            engine.upgradeWithRulesBANK(mps); 
            built=true; 

        }
        
    
    }

    public void setXresource(IXResource xresource) {
        this.xresource = xresource;
    }

    /**
     *
     *
     * @param group
     */
    public void setGroup(String group) {
           this.group = group;
    }

    /**
     * @return 
     */
    public String getGroup() {
           return group;
    }
    
    
    public  void loadFacts(XSmartObject[] facts, int sessId) {
        
           Map<String, XSmartObject> wMemory = wMemoryExtended.get(sessId); 
           if (wMemory == null) {
               wMemory = new ConcurrentSkipListMap<String, XSmartObject>();
               wMemoryExtended.put(sessId, wMemory);
           }
        
           int j = -1;
           for (XSmartObject x : facts) {
               try { 
                   
                 j++; 
                 if (x == null) {
                    XUtils.ilog("log/breerr.log","Error NullFact: "+j);  
                     continue;  
                 }
                 String xn = (String) x.getFieldByName("NAME");            
                 wMemory.put (xn, x);  
                   
               } catch (Throwable ee) {
                XUtils.ilog("log/breerr.log", XUtils.info(ee));  
                 ee.printStackTrace();  
               }
           }
        
    };
    
    public   void loadGlobalFacts(XSmartObject[] facts) {
        
        
           for (XSmartObject x : facts) {
               try { 
                   
                 String xn = (String) x.getFieldByName("NAME");            
                 wMemoryGlobal.put (xn, x);  
                   
               } catch (Throwable ee) {
                 ee.printStackTrace();  
               }
           }
        
    };
    

    
    public  void synchWorkMemory (int sessId) {
        
           int scope=ScriptContext.ENGINE_SCOPE;
           
           String xsessId = "0";
           String fn = "log/synchWorkMemory_"+xsessId+".log";
        XUtils.ilog (fn, "------------------------  XError:  "+engine+" - "+sessId);
           ScriptEngine se = engine.getScriptEngine(sessId);
        XUtils.ilog (fn, "XError:  "+se);

           Map<String, XSmartObject> wMemory = wMemoryExtended.get(sessId); 

           Set <Map.Entry <String, XSmartObject>> vs = wMemoryGlobal.entrySet();
           for (Map.Entry <String, XSmartObject> x: vs ) {
            XUtils.ilog("log/synchWorkMemory_"+xsessId+".log", x.getKey()+"="+x.getValue());    
                se.getContext().setAttribute(x.getKey(),x.getValue(), scope); 
           }

           Set <Map.Entry <String, XSmartObject>> vsl = wMemory.entrySet();
           for (Map.Entry <String, XSmartObject> x: vsl ) {
            XUtils.ilog("log/synchWorkMemory_"+xsessId+".log", x.getKey()+"="+x.getValue());    
                se.getContext().setAttribute(x.getKey(),x.getValue(), scope); 
           }

        
    }
    
    
    public  Map applyRulesSet(String  rulesSet, int sessId) {
        
        Map<String, XSmartObject> wMemory = wMemoryExtended.get(sessId); 
        
        
        long t0 = System.currentTimeMillis();

        XUtils.ilog("log/bre.log", "RulesSet Invoke:  " + built);
        if (!built) {
          
           build();  
           built = true; 
            
        }
        
        synchWorkMemory (sessId);

        XUtils.ilog("log/synchWorkMemory.log", (new Date())+":"+rulesSet);
        XUtils.ilog("log/breerr.log",rulesSet + " : " + sessId);
        XUtils.ilog("log/bre.log", "RulesSet Invoke:  " + mps);
        
        if (mps != null) {

           String code = mps.get(rulesSet);
           String[] s =  rulesSet.split("[:.]");
            XUtils.ilog("log/"+s[s.length-1]+".log", rulesSet+":"+code +" Total Rules: "+mps.size());
           XSmartObject fl = null;

           if (code != null) {
               
           
           try {
           
               ScriptEngine e = engine.getScriptEngine(sessId);
               Bindings bs = e.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
               Set<String> keys =  bs.keySet();
               
               for (String X: keys) {

                        XUtils.ilog("log/bre.log", "X:  " + X+ " - " +bs.get(X));
                   
               }
               
                
               Invocable inv = (Invocable) e;
               Object[] params = new Object[0];
                    XUtils.ilog("log/bre.log", "RulesSet Invoke:  " + rulesSet);
               int nxx = rulesSet.lastIndexOf(".")+1;
               String methName = rulesSet.substring(nxx);
               Object result = inv.invokeFunction(methName, params);
               if (result instanceof XSmartObject) fl = (XSmartObject) result;
                    XUtils.ilog("log/bre.log", "RulesSet:  " + rulesSet+" - "+result);
               if (fl != null)  wMemory.put (rulesSet, fl);
               
           } catch (NoSuchMethodException es) {

                    XUtils.ilog("log/bre.log", "RulesSet:  " + rulesSet+" - "+es.getClass().getName()+":"+es.getMessage());
                    XUtils.ilog("log/bre.log", XUtils.info (es));
             
           } catch (ScriptException ex) {

                    XUtils.ilog("log/bre.log", "RulesSet: " + ex.getLineNumber() + " : " + ex.getColumnNumber() + " / " + rulesSet+" - "+ex.getClass().getName()+":"+ex.getMessage());
                    XUtils.ilog("log/bre.log", XUtils.info (ex));
             
           } catch (Throwable ee) {

                    XUtils.plog("log/error.log", XUtils.info (ee));
       
           }
           
           }

           long dt = System.currentTimeMillis() - t0;
            XUtils.ilog("log/brestat.log",rulesSet + " : " + dt);  
           
        }
        
        return wMemory;

    };
    
    
    public void cleanWM() {
    };

    @Override
    public int openSession() {
           return sessIdCounter.incrementAndGet();
    }

    @Override
    public void closeSession(int sessId) {
        
        try {
        
        if (true) {
           Map<String, XSmartObject> wMemory = wMemoryExtended.get(sessId);
           int scope=ScriptContext.ENGINE_SCOPE;
           Set <Map.Entry <String, XSmartObject>> vs = wMemory.entrySet();
                XUtils.ilog("log/breerr.log"," closeSession : " + sessId);  
           for (Map.Entry <String, XSmartObject> x: vs ) {
                    XUtils.ilog("log/breerr.log",x.getKey() + " : " + sessId);  
                engine.getScriptEngine(sessId).getContext().removeAttribute(x.getKey(),scope); 
           }
           wMemory.clear();
           wMemoryExtended.remove(sessId);
           engine.releaseScriptEngine(sessId);
        }
        
        } catch (Throwable ee) {

            XUtils.ilog("log/breerr.log", XUtils.info(ee));  
            
        }
        
    }


    public String toString () {
        
        
           String s  =  "wMemoryGlobal: " + wMemoryGlobal;
           return s;
        
        
    }
    
    
    public XBREngine() {
           super();
    }
    
}
