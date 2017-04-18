package ru.mtt.bizrules;

import java.time.ZoneId;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.locks.Lock;

import ru.mtt.bizrules.bre.IBREEngine;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;

import org.apache.camel.Processor;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.mtt.webapi.controller.JSONRPCControlObject;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dom.SimpleXSmartObject;
import ru.mtt.webapi.utils.XUtils;

public class InferenceMachine implements Processor {
    
    
    String owner = null;
    Set data = null;
    String ruleSetId;
    
        
    public void setLogName(String logName) {
        this.logName = logName;
        
    }

    public String getLogName() {
        return logName;
    }


    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getRuleSetId() {
        return ruleSetId;
    }
    
    
    String logName = null;
    Map<String, ScriptEngineFactory> factories = new HashMap<String, ScriptEngineFactory>();   
    Map<String, IBREEngine> engines = new ConcurrentHashMap<String, IBREEngine>();
    String defAlias = null;

    public InferenceMachine (List<IBREEngine> contexts) {
     
        this();
     
        for (IBREEngine x: contexts) {
            
             setBreEngine(x);
             defAlias = x.getAlias();
            
        }
    
    
    }


    private String getContextId() {
        
            return IBREEngine._RUBY;
        
    }

    private String getRuleId() {
        
        return "ruby://ru.mtt.ppl";
        
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
           return owner;
    }
    
    public synchronized void init () {
        
           Set <Map.Entry<String, IBREEngine>>  xi = engines.entrySet(); 
           for (Map.Entry<String, IBREEngine> c: xi) {
                IBREEngine xs = c.getValue();  
                XSmartObject[] xx = new XSmartObject[]{};
                xs.loadGlobalFacts(xx);
               
                String xxs = "";
                for (XSmartObject xz: xx) {
                     xxs+=xz+"\n";                  
                }
               
                XUtils.ilog("log/InferenceMachine.log",this.logName+" : "+ xs+" : "+xxs);
           }

    }


    public void reload (String pathId) {
           XUtils.ilog("log/XBREngine.log","BRE: "+owner);
           Set<Map.Entry<String, IBREEngine>> rs =  engines.entrySet();
           for (Map.Entry<String, IBREEngine> x:  rs) {
                IBREEngine eng  = x.getValue();
                eng.load (pathId);
           }
    }

    public void setBreEngine(IBREEngine breEngine) {

        String breAlias = breEngine.getAlias();
        XUtils.ilog("log/inference_sys.log", "Factories: "+factories.size());
        ScriptEngineFactory engine = factories.get(breAlias);
        XUtils.ilog("log/inference_sys.log", "breAlias: "+breAlias);

        if (engine != null) {
            breEngine.setScriptEngineFactory(engine);
            breEngine.init();
            engines.put (breAlias, breEngine);
        }

    }

    public IBREEngine getBreEngine(String alias) {
        
           if (alias == null) return engines.get (defAlias);
           return engines.get (alias);
           
    }

    public InferenceMachine() {
        
           super();
           ScriptEngineManager factory = new ScriptEngineManager();
           List<ScriptEngineFactory> v = factory.getEngineFactories();
           for (ScriptEngineFactory x: v) {
                String nm = x.getLanguageName().toLowerCase();
                factories.put(nm, x); 
                XUtils.ilog("log/inference_sys.log", nm+": "+x+":"+x.getLanguageName());

           }

    }


    public Object executeRule(Object IN) {
        
        IBREEngine eng = this.getBreEngine(getContextId());
        int sessId = eng.openSession ();
        
        int nx = (data!= null)?data.size():0; 
        
        XSmartObject [] arr = new XSmartObject [nx+1];
        int i = 0;
        if (data != null) {
        Iterator its = data.iterator();
        while(its.hasNext()) {
              arr [i++] = (XSmartObject) its.next();
        }
        }
        
        arr[i] = new SimpleXSmartObject("$IN", IN);
        
        eng.loadFacts(arr, sessId);           
        Map wMEMORY = eng.applyRulesSet(getRuleId(), sessId);
        XSmartObject res = null;
        XUtils.ilog("log/PortaFilePooler2.log","wMEMORY: "+wMEMORY);
        if (wMEMORY != null) {
            res = (XSmartObject) wMEMORY.get(getRuleId());
        }
        XUtils.ilog("log/PortaFilePooler2.log","Result: "+res);
        eng.closeSession (sessId);
        return res; 
        
        
    }


    
    @Override
    public synchronized void process(Exchange exchange) throws Exception {
        
        try {
           String qs = exchange.getIn().getBody(String.class);
           System.out.println(qs);

           Map<String, Object> hdr = exchange.getIn().getHeaders(); 
           org.apache.camel.component.http.HttpMessage  msg = (org.apache.camel.component.http.HttpMessage) exchange.getIn();
           HttpServletRequest req = msg.getRequest();
           String method = req.getMethod(); 
           String uri = req.getContextPath();

           String outMessage = "{}";
           if ("GET".equals(method.toUpperCase())) {

                
           }  else {







               outMessage = qs;   
                
           }
        
           exchange.getOut().setHeader("Content-Type", "application/json");
           exchange.getOut().setHeader("Content-Length", outMessage.getBytes().length);
           exchange.getOut().setHeader("Content-Encoding", "UTF-8");
           exchange.getOut().setHeader("exitcode", 0);            
           exchange.getOut().setBody(outMessage);
            
        } catch (Throwable ee) {

          ee.printStackTrace();
          
        }
            
    }
    
    
    public void setMEMO(Set data) {
        
           this.data = data;
        
    }
    
    
    public static void main (String[] arg) {
        
        try {


           // 0000053C_8C9A11E6_9918AC16_2D8CE148_1   
           ArrayList<Integer> xs = new  ArrayList<Integer> ();
           Date d1 = new Date();
           long t1 = 1481023503000L; 
           long t0 = 1481023503000L; 
           t0 = 1473167728000L;
               
               
           String[] v  = TimeZone.getAvailableIDs();
           for (String x: v)  {
                System.out.println (x); 
           }
            
            
           ZoneId zoneMSC = ZoneId.systemDefault(); 
           System.out.println ("x_MSK: "+zoneMSC.getId()); 
           ZonedDateTime now = ZonedDateTime.now ( zoneMSC );
           System.out.println ("x_MSK: "+now.getHour() +" - "+ now.getMinute() + " - "+ now.getSecond()); 
           System.out.println ("x_MSK: "+now.toEpochSecond()); 

           ZonedDateTime nowUtc = now.withZoneSameInstant( ZoneOffset.UTC ); 
           System.out.println ("x_UTC: "+nowUtc.getHour() +" - "+ nowUtc.getMinute() + " - "+ nowUtc.getSecond()); 
            
           long x = 1481634596000L;
           d1.setTime(x); 
            
           Calendar cc = Calendar.getInstance(TimeZone.getDefault());
           cc.setTimeInMillis(System.currentTimeMillis());
           System.out.println ("x_MSK: "+cc.getTimeInMillis()+" - "+TimeZone.getAvailableIDs()); 
           cc.setTimeZone(TimeZone.getTimeZone("UTC"));
            
           System.out.println ("x_UTC: "+cc.getTimeInMillis()); 
 
           d1.setTime(t0); 
           System.out.println ("t0: "+d1); 

           d1.setTime(t1); 
           System.out.println ("t1: "+d1); 
            
            
        } catch (Throwable ee) {
            
           ee.printStackTrace(); 
        
        }
        
    }

     
    public static void mainxx( String[] args ) {

            ScriptEngineManager mgr = new ScriptEngineManager();
            List<ScriptEngineFactory> factories = mgr.getEngineFactories();

            for (ScriptEngineFactory factory : factories) {

                System.out.println("ScriptEngineFactory Info");

                String engName = factory.getEngineName();
                String engVersion = factory.getEngineVersion();
                String langName = factory.getLanguageName();
                String langVersion = factory.getLanguageVersion();

                System.out.printf("Script Engine_1: " + factory.getScriptEngine());
                System.out.printf("Script Engine_2: " + factory.getScriptEngine());

                System.out.printf("\tScript Engine: %s (%s)%n", engName, engVersion);

                List<String> engNames = factory.getNames();
                for(String name : engNames) {
                    System.out.printf("\tEngine Alias: %s%n", name);
                }

                System.out.printf("\tLanguage: %s (%s)%n", langName, langVersion);

            }

    }

    
    
}
