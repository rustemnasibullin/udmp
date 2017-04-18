package org.mtt.webapi.xresource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mtt.webapi.controller.IJSONRPCControlObject;
import org.mtt.webapi.controller.JSONRPCControlObject;
import org.mtt.webapi.core.IConstants;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XCloudService;
import org.mtt.webapi.core.XMap;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.utils.Curl;
import org.mtt.webapi.utils.XUtils;

public class XResourceProxy extends XCloudService implements  IXResource, IConstants {
    
    
    
    
    public XResourceProxy() {
        super();
    }

    @Override
    public String getDefaultAlias() {
           return this._XRepository;
    }

    @Override
    public String getDefaultHost() {
        
           String xs = this.getConfigParameter("XRESOURCE_HOST");
           if (xs == null) return "127.0.0.1";
           return xs;
    
    }

    @Override
    public int getDefaultPort() {
        return 19771;
    }

    @Override
    public String getDefaultURI() {
        return "xresource";
    }

    @Override
    public String getResourceByPath(String path) {
        
        XSmartObject res = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("getResourceByPath");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path});
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, cntr.toString());
        res = uCurl.execute();
        String xs = null;
        
        if (res != null) {
            
            try {    
                
              xs = (String) res.getFieldByName("VALUE");
                
            } catch (Throwable ee) {
                
              ee.printStackTrace();   
              
            }
        
        }
        
        return xs;
    
    }

    @Override
    public List<String> getResourceByPathAsList(String path) {
        
        XSmartObject res = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("getResourceByPathAsList");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams (new String[]{path});
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, cntr.toString());
        res = uCurl.execute(); 
        return Collections.emptyList();

    }

    @Override
    public Map<String, String> getResourceByPathAsMAP(String path) {
        
        XSmartObject res = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("getResourceByPathAsMAP");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path});
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, cntr.toString());
        res = uCurl.execute(); 
        return Collections.emptyMap();

    }

    @Override
    public void updateResourceByPath(String path, String data) throws WAPIException {
        
        XSmartObject res = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("updateResourceByPath");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path, data});
        String data2post = cntr.toString();
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, data2post);
        res = uCurl.execute(); 
        
    }
    
    
    public List<String> getFolderByPathAsList(String path) {

        XSmartObject res = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("getFolderByPathAsList");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path});
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, cntr.toString());
        res = uCurl.execute(); 
        return Collections.emptyList();

    };
    
    public Map<String, String> getFolderByPathAsMAP(String path) {
    
        XSmartObject res = null;
        Map<String, String> rMap = null;
        Curl uCurl = this.createHTTPClientForService(null);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("getFolderByPathAsMAP");
        cntr.setId("1");
        cntr.setJsonrpc("2.0");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path});
        String data2post = cntr.toString();
        uCurl.setOpt(Curl.CURLOPT_POSTFIELDS, data2post);
        XUtils.ilog ("log/xresource.log","Data: "+data2post); 

        res = uCurl.execute(); 
        
        try {

            if (res != null) {
                String data = (String) res.getFieldByName("VALUE");
                if (!XUtils.isEmpty(data)) {
                    XUtils.ilog ("log/xresource.log","DataOut: "+data); 
                    IJSONRPCControlObject cntr0 = XUtils.toJSONRPCControl(data.trim()+"\n");   
                    Object ds = cntr0.getResult();    
                    rMap = (Map<String, String>) ds;
                }
            }

        } catch (Throwable ee) {

            XUtils.ilog ("log/xresource.log", XUtils.info(ee)); 
          
        }
        
        return rMap;
    
    }; 

    
    
    public  static void main (String [] x) {
        
        XResourceProxy cx = new XResourceProxy();
        String xs = cx.getResourceByPath("groovy://ru.mtt.icloud.bizrules.cdrdiscover_rule");
        System.out.println (xs);
        
    }

}
