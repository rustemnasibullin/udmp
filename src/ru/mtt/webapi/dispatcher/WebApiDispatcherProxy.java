package ru.mtt.webapi.dispatcher;

import com.google.gson.Gson;
import java.util.ArrayList;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.utils.Curl;
import ru.mtt.webapi.utils.XUtils;

public class WebApiDispatcherProxy extends XConfigurableObject  {

    boolean usePrototype = false;
    Curl curl = null;
    String host = null;
    int port = 0;

    String host_asynch = null;
    int port_asynch = 0;
    
    
    public String[] routeXResourceNotifications (String body) {
        
           String [] xs = null;
           XUtils.ilog("log/WebApiDispatcherProxy.log", body);  
           
           String[] xResourceSubcriber = this.getConfigParameter("xresource.subscribers").split("[,]");

           int k = 0; 
           ArrayList<String> ts = new ArrayList<String> ();
           for (String serviceAlias: xResourceSubcriber) { 

                    XUtils.ilog("log/WebApiDispatcherProxy.log", serviceAlias+":"); 
                    String[] xs1 = null;
                    
                    if (usePrototype) {
                    xs1 = getCloudServiceInfoDatagrammasWithUsePrototype(serviceAlias);
                    } else  {
                    xs1 = getCloudServiceInfoDatagrammas(serviceAlias);
                    }
                    
                    for (String x: xs1) {
                         ts.add(x);
                         k++;
                    }
                         
           }
           
           int ns = ts.size();
           xs = ts.toArray(new String[ns]);
           for (String s: xs) {
                XUtils.ilog("log/WebApiDispatcherProxy.log", s); 
           }

           return xs;
        
    }

    public void doConfig () {
        
           host = this.getConfigParameter("webapidispatcher.host");
           port = this.getIntConfigParameter("webapidispatcher.port");
      
           host_asynch = this.getConfigParameter("AMQ_HOST");
           port_asynch = this.getIntConfigParameter("AMQ_PORT");
      
           curl =  new Curl();
           curl.setOpt(Curl.CURLOPT_URL, "http://"+host+":"+port);
        
    }

    //@spike
    public String[] getServicesByDomainCrutchFunction (String domain) {
           String[] x = null;
           String uri = null;
           XUtils.ilog("log/xcloudservice.log", domain);

        
           switch(domain) {
           case "RC":
           case "NTX":
           case "NTX_init":
           case "Parking":
           case "Parking_init":
               uri = this.getConfigParameter("FILESTORAGE_MNG_RC");    
           break;

           case "d1.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG1");    
           break;

           case "d2.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG2");    
           break;

           case "d3.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG3");    
           break;

           case "d1-cp.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG1");    
           break;

           case "d2-cp.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG2");    
           break;

           case "d3-cp.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG3");    
           break;

           case "public.d1.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG_ZIP1");    
           break;

           case "public.d2.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG_ZIP2");    
           break;

           case "public.d3.mtt.ru":
               uri = this.getConfigParameter("FILESTORAGE_MNG_ZIP3");    
           break;
            
           }
           
           XUtils.ilog("log/xcloudservice.log", uri);

           x = parseUri(uri);
           
           return x; 
    };   

    private String[] parseUri(String uri) {
            String[] x = null;
            if (uri != null) {
                
                
                int iport = getIntConfigParameter("download.port");
                
                String[] xs = uri.split("[:?&]"); //mina2:tcp://127.0.0.1:8093?textline=true&sync=true
                String host = xs[2];
                String port = xs[3];
                host = host.replaceAll("/","");
                String luri= "/";
                String value  = "100";
                x = new String[]{"host="+host+",port="+iport+",uri="+luri+",af="+value};
            }
            return x; 
    };


    public String[] getCloudServiceInfoDatagrammas(String serviceAlias) {
        
           String[] x = null;

           XUtils.ilog("log/xcloudservice.log", "Found Service: "+serviceAlias);

           
           if (serviceAlias.startsWith("topic:") || serviceAlias.startsWith("queue:")) {
               
           x = new String[] {"host="+host_asynch+",port="+port_asynch+",af=1.0,uri="+serviceAlias};

           } else {
           
           curl.setOpt(Curl.CURLOPT_POSTFIELDS, "TYPE=JSON&ALIAS="+serviceAlias);
           XSmartObject val = curl.execute();
           
           try {
           
             String err = (String) val.getFieldByName("NAME"); 
             String v = (String) val.getFieldByName("VALUE"); 
               
               
             if (v != null && (!err.equals("error"))) {  
                 
                 Gson GS = new Gson();
                 x = GS.fromJson(v, String[].class);

             }
               
           } catch (Throwable ee) {
             ee.printStackTrace();     
           }
               
           }
 
           return x;
        
    }
    

    public String[] getCloudServiceInfoDatagrammasWithUsePrototype(String serviceAlias) {
        
           String[] x = null;
           return x;
        
    }
    
    
    public void  start () {
        
        try {

            doConfig();

        } catch (Throwable ee) {
            ee.printStackTrace();
        }
        
        
    }
    
    
    public WebApiDispatcherProxy() {
        super();
    }


    public void stop() {
        // TODO Implement this method
    }



    public static void main (String[] x) {
           String[] xx = new WebApiDispatcherProxy().parseUri("mina2:tcp://127.0.0.1:8794?textline=true&sync=false");
           System.out.println(xx);
    }

}
