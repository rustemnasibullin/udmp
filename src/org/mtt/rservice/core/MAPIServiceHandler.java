package org.mtt.rservice.core;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.mina.util.ConcurrentHashSet;

import org.mtt.webapi.core.IConstants;
import org.mtt.webapi.dispatcher.WebApiDispatcherMBean;
import org.mtt.webapi.utils.XUtils;

/**
 *  Simple Service handler - control dsimple services for monitoring and acquisition statistics
 *
 *  @author rnasibullin@mtt.ru  Chief
 */

public class MAPIServiceHandler implements Serializable, IConstants {
    
    Logger log = Logger.getLogger (MAPIServiceHandler.class);
    final public static String[] kpi = new String[] { IConstants._FREQ, IConstants._RESPT}; 
    final protected static Set<String> meths = new ConcurrentHashSet<String> ();
    long lastVisit = 0L;
    boolean enabled = true;
    String host = "127.0.0.1";
    int port = 7703;
    ObjectName name = null;
    JMXServiceURL url = null;
    JMXConnector jmxc = null;
    MBeanServerConnection mbsc = null;
    WebApiDispatcherMBean mxbeanProxy = null;
    boolean changed = true;
    Map <String, Map <Long, Double>> data = new ConcurrentHashMap <String, Map <Long, Double>>();
    Map <String, Double> services = new ConcurrentHashMap <String, Double>();
    
    public static Set<String> getMethods() {
        
           return meths;    
        
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setHost(String host) {
        if (!host.equals(this.host)) changed = true;
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        if (port != this.port) changed = true;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MAPIServiceHandler() {
        
        super();
        try {
              name = new ObjectName ("ru.mtt.webapi.dispatcher:type=WebApiDispatcher" );
        } catch (Throwable ee) {
            ee.printStackTrace();
        }

    }


 


    public double getAvailabilityFactor() {
        
        double d = 0.0;
        
        try {

            if (changed) {

            url = new JMXServiceURL ("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url);
                XUtils.ilog("log/_handler2.log", "> "+jmxc);
            mbsc = jmxc.getMBeanServerConnection();
            mxbeanProxy = JMX.newMXBeanProxy(mbsc, name, WebApiDispatcherMBean.class);
            
            }
            
            if (mxbeanProxy != null) d = mxbeanProxy.getAvailabilityFactor();
            
    } catch (Throwable ee) {

            XUtils.ilog("log/_handler2.log", XUtils.info (ee));
        
    }


    return d; 


    }

    public double getAvailabilityFactor(String service) {
        
           return services.get (service);

    }

    public void retreiveData() {
        
        try {

            if (changed) {

            url = new JMXServiceURL ("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url);
            mbsc = jmxc.getMBeanServerConnection();
            mxbeanProxy = JMX.newMXBeanProxy(mbsc, name, WebApiDispatcherMBean.class);
            
            }



            // retreive data for 
            if (mxbeanProxy != null) {
            String[] serviceIds = mxbeanProxy.getServices();
            String[] methods = mxbeanProxy.getMethods();


                XUtils.ilog("log/_methodsdisp.log", ":"+methods);
            
            
            services.clear();
            for (String sc: serviceIds) {
                 if (sc != null) {
                 Double val = mxbeanProxy.getAvailability(sc);
                 if (val != null) services.put (sc,val);
                 }
            }
            
            for (String x: methods) {
                 meths.add(x);
            }
                

            for (String m: meths) {

                 for (String kpii: kpi) {
                 String s1 =  m + "."+ kpii; 
                     
                 log.debug(host+":   Retreive kpi for method: "+s1);
                     
                 String[] x = mxbeanProxy.getMetricsValueTrendDataGrammas(kpii, m, lastVisit);

                 if (x != null) {     
                 
                 Map <Long, Double> trend1 = data.get (s1);
                     
                 if (trend1 == null) {
                     trend1 = new TreeMap <Long, Double>();
                     data.put (s1, trend1);
                 }
                 
                 for (String s: x) { 
                     
                      int nx = s.indexOf(":");
                      String key = s.substring(0,nx).trim();
                      String val = s.substring(nx+1).trim();
                      lastVisit = Long.parseLong(key);
                      double value = Double.parseDouble(val);
                      trend1.put (lastVisit, value);
                     
                 }
                 
                 
                 }
                     
                     
            }
                
            


                // clean odd data
                for (String kpii: kpi) {
                String s1 =  m + "."+ kpii; 
                Map <Long, Double> trend1 = data.get (s1);
                    
                if (trend1.size() > IConstants._NMaXTrend) {
                        
                    int dx = trend1.size() - IConstants._NMaXTrend;
                    
                    Collection<Long> kss = trend1.keySet();
                    int i = 0;
                    ArrayList<Long> lst = new ArrayList<Long> ();
                    for (Long key : kss) {
                         if (i<dx) {
                             lst.add (key);
                             i++;
                         } else {
                             break;
                         }                         
                    }

                    for (Long key : lst) {
                    trend1.remove(key);
                    }

                }
                    
                }

   
            }
            
            }
            
            changed = false;
            
        } catch (Throwable ee) {

            XUtils.ilog("log/_handler1.log", XUtils.info (ee));
            
        }
        
        log.debug("Retreived : "+data);


    }


    public void retreiveData2() {
        
        try {

            if (changed) {

            url = new JMXServiceURL ("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url);
            mbsc = jmxc.getMBeanServerConnection();
            mxbeanProxy = JMX.newMXBeanProxy(mbsc, name, WebApiDispatcherMBean.class);
            
            }
            
            long ts = System.currentTimeMillis();

            for (String m: meths) {
    
                 for (String kpii: kpi) {
                 double x = mxbeanProxy.getMetricValue(kpii, m);
                 String s1 =  m + kpii; 
                 Map <Long, Double> trend1 = data.get (s1);
                     
                 if (trend1 == null) {
                     trend1 = new TreeMap <Long, Double>();
                     data.put (s1, trend1);
                 }
                     
                 trend1.put (ts, x);
                 
                 }
    
            }
            
//            changed = false;
            changed = true;
            
        } catch (Throwable ee) {
            ee.printStackTrace();
        }

    }




    public SortedMap <Long, Double>  getData (String metric, String method, Date d1, Date d2) {

           SortedMap <Long, Double> res = new java.util.concurrent.ConcurrentSkipListMap<Long, Double> ();
           long d1L = 0;
           if (d1 != null) {
               d1L = d1.getTime();
           }

           long d2L = 0;
           if (d2 != null) {
               d2L = d2.getTime();
           } 
           
           Map <Long, Double> datai = data.get(method+"."+metric);
           
 
           
           if (datai == null) {
               datai =  new TreeMap <Long, Double> ();
               data.put(method+"."+metric, datai);
           }
           
           
           Set<Long> xs = datai.keySet();
           
           for (Long xx: xs) {

               if (d1L>0 && d2L>0 && xx>=d1L && xx<=d2L) {
                   res.put (xx, datai.get(xx));
               } else if (d1L==0 && d2L==0) {
                   res.put (xx, datai.get(xx));
               } else {
               }

           }
           
           return res;
                     
    }


}
