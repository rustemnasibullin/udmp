package org.mtt.webapi.dom;

import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.utils.Sys;

public class CServiceInfo extends XSmartObject {
    
    public static final String _HOST = "host";
    public static final String _PORT = "port";
    public static final String _URI  = "uri";
    public static final String _AF   = "af";
    
    int port = 0;
    String host = null;
    String uri = null;
    double avFactor = 0.0;
    String alias = null;
    Long timeStamp = null;

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setAvFactor(double avFactor) {
        this.avFactor = avFactor;
    }

    public double getAvFactor() {
        return avFactor;
    }

    public CServiceInfo() {
        super();
    }

    @Override
    public Object getFieldByName(String NAME) throws WAPIException {
           if ("ID".equals(NAME)) {
               return alias;
           } else if ("TIMESTAMP".equals(NAME)) {
               return timeStamp;
           }
           return null;
    }

    @Override
    public void setFieldByName(String n, Object v) throws WAPIException {
        
           if ("DATAGRAMMA".equals(n)){
                String xs = v.toString();
                String xx[] = xs.split("[,]");
                
                for (String xi: xx) {
                Sys.out("Datagramme:"+xi);
                     int nx = xi.indexOf("=");
                     if (nx>0) {

                     String xn = xi.substring(0,nx); 
                     String xv = xi.substring(nx+1);
                     switch (xn) {
                     case _HOST:
                     host = xv;    
                     break;    
                     case _PORT:
                     port = Integer.parseInt(xv);    
                     break;    
                     case _URI:
                     uri = (String)xv;    
                     break;    
                     case _AF:
                     avFactor = Double.parseDouble(xv);    
                     break;    
                     }

                     }
                    
                }
                
           } else if ("ID".equals(n)) {
             alias = v.toString();
           } else if ("TIMESTAMP".equals(n)) {
             timeStamp = (Long) v;
           }

    }
     
    public static void main (String[] x) {
        
           String xs = "[]";
           String xx[] = xs.split("[,]");
        
           for (String xi: xx) {
            Sys.out("Datagramme:"+xi);
           }
        
    }

    @Override
    public String toJSONString() {
           String s = "{'alias':'"+alias+"','host':'"+host+"','port':" +port+",'uri':'"+uri+"','timeStamp':'"+timeStamp+"}";
           return s;
    }
    
}
