package ru.mtt.rservice.core;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import ru.mtt.webapi.core.IMemCache;
import ru.mtt.webapi.utils.XUtils;

/**
 *  ServiceFarn handler - control distributed services for monitoring and acquisition statistics
 *
 *  @author rnasibullin@mtt.ru  Chief
 */

public class MAPIServiceFarmHandler {
    
    static MAPIServiceFarmHandler instance = null;
    ConcurrentHashMap <String, MAPIServiceHandler> mapis = new ConcurrentHashMap <String, MAPIServiceHandler>();
    ConcurrentHashMap <String, MAPIServiceHandler> handlers = new ConcurrentHashMap <String, MAPIServiceHandler>();
    IMemCache cache;
    
    public void stopScanService(String OID) {
        
           MAPIServiceHandler MS = mapis.remove(OID);
           XUtils.ilog("log/_mapipservicefarm.log", "StopScanService: " + OID + ":" );
           MS.setEnabled (false);
           
    };

    public void suspendPromoteUsageService(String OID) {
        
           MAPIServiceHandler MS = mapis.get(OID);
           //  if (MS != null) MS.setEnabled(false);
           XUtils.ilog("log/_mapipservicefarm.log", "suspendUseService: " + OID + ":" );
           
    };

    public ConcurrentHashMap<String, MAPIServiceHandler> getMapis() {
           return mapis;
    }

    public void setCache(IMemCache cache) {
           this.cache = cache;
    }

    public IMemCache getCache() {
           return cache;
    }

    public void startScanService(String OID, String jmxHost, int jmxPort) {
        
           MAPIServiceHandler MS = handlers.get (jmxHost+":"+jmxPort);
           XUtils.ilog("log/_mapipservicefarm.log", "startScanService: " + OID + ":" + jmxHost + ":" + jmxPort);
           if (MS == null) {
               MS = new MAPIServiceHandler(); 
               MS.setHost(jmxHost);
               MS.setPort(jmxPort);
               handlers.put (jmxHost+":"+jmxPort, MS);
           }
           if (MS != null) MS.setEnabled(true);
           mapis.put (OID, MS);
           
    };
    
    public void actualizeService(String OID, double avFactor) {
    };
    
    public synchronized static MAPIServiceFarmHandler getInstance() {
           if (instance == null) instance = new MAPIServiceFarmHandler();
           return instance; 
    }
    
    public MAPIServiceHandler getMAPIServiceHandler(String h) {
           return  mapis.get (h);  
    };

    
    private MAPIServiceFarmHandler() {
        
            super();
    
    }
    
    public void uploadActiveStatistics() {
        
           Enumeration <MAPIServiceHandler> enm = mapis.elements();
           XUtils.ilog("log/_mapipservicefarm.log", "MAPIService: "+ mapis.size());
           XUtils.ilog("log/_mapipservicefarm.log", "MAPIServiceHandlers: "+ handlers.size());
           
           while (enm.hasMoreElements()) {
               
                  MAPIServiceHandler srv = enm.nextElement();
                  if (srv.isEnabled()) {
                      srv.retreiveData(); 
                  }  
               
           }
    
    };
    
}
