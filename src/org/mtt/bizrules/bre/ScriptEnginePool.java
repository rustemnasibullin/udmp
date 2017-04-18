package org.mtt.bizrules.bre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import javax.script.ScriptEngineManager;

import org.mtt.webapi.core.IConstants;
import org.mtt.webapi.utils.XUtils;

public class ScriptEnginePool implements IConstants {
    
    public static final String _LOGFILE = "log/script_engine_pool.log";
    public static final String _LOGFILE2 = "log/script_engine_pool2.log";
    
    int poolSize = 50;
    ScriptEngineFactory factory = null;
    ConcurrentHashMap <Integer, ScriptEngine> enginesOnWork = new ConcurrentHashMap <Integer, ScriptEngine> ();
    ArrayBlockingQueue<ScriptEngine> engines = new ArrayBlockingQueue <ScriptEngine> (poolSize, true);
    boolean initilized = false;
    
    public ScriptEnginePool() {
           super();
    }

    public synchronized void upgradeWithRulesBANK(Map <String, String> mps) {
        
        
        Collection<String> vals = mps.values();
        for (ScriptEngine e: engines) {
 
            for (String xe: vals) {
                try {
                    
                  e.eval(xe);
                    
                } catch (Throwable ee) {

                    XUtils.ilog (_LOGFILE, xe);
                    XUtils.ilog (_LOGFILE, XUtils.info(ee));
                  
                }
            }
                         
        }
    
    }; 


    public void setInitilized(boolean initilized) {
        this.initilized = initilized;
    }

    public boolean isInitilized() {
        return initilized;
    }

    public void init () {
        
        if (factory != null) {
        for (int i=0; i<poolSize; i++) {
            try { 
                engines.put(factory.getScriptEngine());
            } catch (InterruptedException ex) {
                    XUtils.ilog(_LOGFILE, "Cannot put ScriptEngine to pool");
                    XUtils.ilog(_LOGFILE, XUtils.info(ex));
            }
        }
        initilized = true;
            XUtils.ilog (_LOGFILE, "Init: "+engines.size());    
        }

    }
    
    public ScriptEngine getScriptEngine () {        
        try {
            return engines.take();
        } catch (InterruptedException ex) {
            XUtils.ilog(_LOGFILE, "Cannot take ScriptEngine from pool\n" + XUtils.info(ex));
        } 
        return null;
    }

    public ScriptEngine getScriptEngine (int sessId) {
           ScriptEngine e = enginesOnWork.get (sessId);
           if (e == null) {
               e = getScriptEngine ();
            XUtils.ilog(_LOGFILE, sessId+"="+e);
               enginesOnWork.put (sessId, e);
           }
           return e;        
    }

    public void releaseScriptEngine (int sessId) {
        
           ScriptEngine e = enginesOnWork.get (sessId);
            try { 
                engines.put(e);
            } catch (InterruptedException ex) {
            XUtils.ilog(_LOGFILE, "Cannot put ScriptEngine to pool\n" + XUtils.info(ex));
            }
           enginesOnWork.remove(sessId);
        XUtils.ilog(_LOGFILE2, "Release " + sessId + " := " + e);

    }
    
    
    static volatile int i = 0;
    public static void main (String[] x)  {
        
        ScriptEnginePool sep = new ScriptEnginePool  ();
        ScriptEngineManager f = new ScriptEngineManager();
        List<ScriptEngineFactory> fi = f.getEngineFactories();
        sep.factory = fi.get(0);
        sep.init();
        
       
        for (i=0; i<50; i++) {
        
            Thread t = new Thread (new Runnable() {
                int j = i;
                   
                public void run() {
                    ScriptEngine ee = sep.getScriptEngine(j);
                    XUtils.ilog(_LOGFILE2, "Started: "+ j+":="+ee);
                    try {
                    Thread.currentThread().sleep (10);
                    } catch (Exception ex) {
                        
                    }
                    sep.releaseScriptEngine (j); 
                }
                   
            });
            t.start ();
        
            
        }
        
        
        
    }
    
    
}
