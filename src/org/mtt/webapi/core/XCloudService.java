package org.mtt.webapi.core;

import java.util.HashMap;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.springframework.jms.core.JmsTemplate;

import org.mtt.webapi.dom.CServiceInfo;
import org.mtt.webapi.dispatcher.WebApiDispatcherMBean;
import org.mtt.webapi.dispatcher.WebApiDispatcherProxy;
import org.mtt.webapi.utils.Curl;
import org.mtt.webapi.utils.MQClient;
import org.mtt.webapi.utils.XUtils;

public abstract class XCloudService extends XConfigurableObject implements IConstants {
    
    abstract public String getDefaultHost();
    abstract public int getDefaultPort();
    abstract public String getDefaultURI();
    abstract public String getDefaultAlias();
     
    static  HashMap <String, String[]> lCache = new HashMap <String, String[]> ();
    WebApiDispatcherProxy webApiDispatcher = null;
    MQClient asynchProxy = null;

    public String[] getCloudServiceInfoDatagrammas (String als) {
        
           return lCache.get (als);
            
    };
    
    public void cacheCloudServiceInfoDatagrammas (String als, String[] xs) {
        
           if (xs != null) lCache.put (als, xs);
        
    };

    public void setAsynchProxy(MQClient asynchProxy) {
        this.asynchProxy = asynchProxy;
    }

    public MQClient getAsynchProxy() {
        return asynchProxy;
    }

    public MQClient createMQClientForService(String alias) {
           String als = this.getDefaultAlias();
           if (alias != null) als = alias; 
            
           MQClient mq = new MQClient ();
           
           String[] xs = getCloudServiceInfoDatagrammas (als);
           if  (xs == null && this.getWebApiDispatcher() != null) {
           xs = this.getWebApiDispatcher().getCloudServiceInfoDatagrammas(als);
           cacheCloudServiceInfoDatagrammas (als, xs);
           } 
           
           if  (this.getWebApiDispatcher() == null) {
               xs = new String[]{"host=172.16.104.8,port=61616,af=1.0,uri=topic:completions"};
           }
           
           
           int port = 0;
           String host = null;
           String uri = null;
        
           if (xs == null || xs.length == 0) {

               port = this.getDefaultPort();
               host = this.getDefaultHost();
               uri  = this.getDefaultURI();
            XUtils.ilog("log/icloud.log",alias+": 1 "+ host);
                           
           } else {
               
               CServiceInfo info = this.discoverCloud(xs);
               port = info.getPort();
               host = info.getHost();
               uri  = info.getUri();
            XUtils.ilog("log/icloud.log",alias+": 2 "+ uri);
              
               
           }

        XUtils.ilog("log/icloud.log",alias+": 2 "+ uri);
           
           mq.setDestination(uri);
           
           
           String xURL = "tcp://"+host+":"+port;
        XUtils.ilog("log/icloud.log",alias+": 2 "+ xURL);
           ActiveMQConnectionFactory factory=new ActiveMQConnectionFactory(xURL);
           factory.setWatchTopicAdvisories(false);
           JmsTemplate jmsTemplate  = new JmsTemplate  (factory);  
           
           if (uri.startsWith("topic:")) {
             
             String name = uri.substring(6); 
             org.apache.activemq.command.ActiveMQTopic TOPIC = new org.apache.activemq.command.ActiveMQTopic (name);
             factory.setSendAcksAsync(false);
             TOPIC.setPhysicalName(name);
             jmsTemplate.setDefaultDestination (TOPIC);
            XUtils.ilog("log/icloud.log",alias+": 4 "+ name);
               
           } else {
               
             org.apache.activemq.command.ActiveMQQueue QUEUE = new org.apache.activemq.command.ActiveMQQueue (uri);
             factory.setSendAcksAsync(false);
             QUEUE.setPhysicalName(uri);
             jmsTemplate.setDefaultDestination (QUEUE);
               
           }
           
           mq.setTemplate(jmsTemplate);
           
           return mq;
           
    };

    public Curl createHTTPClientForService (String alias) {
        
        String als = this.getDefaultAlias();
        if (alias != null) als = alias; 
        
        String[] xs = null;

        XUtils.ilog("log/xcloud_srv.log", "this.getWebApiDispatcher(): "+this.getWebApiDispatcher());        
        if (this.getWebApiDispatcher() != null) {
        xs = this.getWebApiDispatcher().getCloudServiceInfoDatagrammas(als);
        }
        XUtils.ilog("log/xcloud_srv.log", als+ "  getCloudServiceInfoDatagrammas: "+xs);        
        
        int port = 0;
        String host = null;
        String uri = null;
       
        if (xs == null || xs.length == 0) {

            XUtils.ilog("log/xcloud_srv.log", als+ "  getCloudServiceInfoDatagrammas1 ");        
            port = this.getDefaultPort();
            host = this.getDefaultHost();
            uri  = this.getDefaultURI();
            
        } else {
            
            CServiceInfo info = this.discoverCloud(xs);
            XUtils.ilog("log/xcloud_srv.log", " discoverCloud "+info);        
            port = info.getPort();
            host = info.getHost();
            uri  = info.getUri();
        
        }
        
        Curl c = new Curl();
        XUtils.ilog("log/xcloud_srv.log", "http://"+host+":"+port+"/"+uri);        
        c.setOpt(Curl.CURLOPT_URL, "http://"+host+":"+port+"/"+uri);
        c.setOpt(Curl.CURLOPT_POST, 1);
        return c;

    }


    public Curl createTCPClientForService (String alias) {
        
        String als = this.getDefaultAlias();
        if (alias != null) als = alias;

        XUtils.ilog("log/DownLoadBalancer.log", "XCloudService for: "+ alias);
        String[] xs = this.getWebApiDispatcher().getCloudServiceInfoDatagrammas(als);
        XUtils.ilog("log/DownLoadBalancer.log", "XCloudService: tcp://"+ xs);
        
        int port = 0;
        String host = null;
        String uri = null;
       
        if (xs == null || xs.length == 0) {

            port = this.getDefaultPort();
            host = this.getDefaultHost();
            uri  = this.getDefaultURI();
            
        } else {
            
            CServiceInfo info = this.discoverCloud(xs);
            port = info.getPort();
            host = info.getHost();
            uri  = info.getUri();
            
            
        
        }
        
        Curl c = new Curl();
        XUtils.ilog("log/DownLoadBalancer.log", "XCloudService: tcp://"+host+":"+port);
        c.setOpt(Curl.CURLOPT_URL, "tcp://"+host+":"+port);
        return c;

    }

    
    private CServiceInfo discoverCloud (String[] xs) {    
    
    CServiceInfo info = null;
    for (String vs: xs) {

            XUtils.ilog("log/discoverCloud.log", "discoverCloud DATAGRAMMA: "+ vs);
         CServiceInfo xinfo = new CServiceInfo  ();
        
         try { 
           
           xinfo.setFieldByName("DATAGRAMMA", vs);
             
           if (info == null) {
           info = xinfo;      
           } else {
           if (xinfo.getAvFactor()>info.getAvFactor())  info = xinfo;    
           }
             
         } catch (WAPIException ee) {
           ee.printStackTrace();  
         }
        
    }

        XUtils.ilog("log/discoverCloud.log", "discoverCloud info: "+ info);
    return info;
    
    }
    



    public void setWebApiDispatcher(WebApiDispatcherProxy webApiDispatcher) {
        this.webApiDispatcher = webApiDispatcher;
    }

    public WebApiDispatcherProxy getWebApiDispatcher() {
        return webApiDispatcher;
    }

    public XCloudService() {
        super();
    }
    
    public void doConfig () {
        
    }
    
    
}
