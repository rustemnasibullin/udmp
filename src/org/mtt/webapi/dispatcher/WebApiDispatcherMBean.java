package org.mtt.webapi.dispatcher;

import java.util.List;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.management.openmbean.TabularData;

import org.mtt.rservice.core.Quantil;
/**
 * MBEAN Controlling interface
 * 
 * @author rnasibullin@mtt.ru
 */

public interface WebApiDispatcherMBean {
    
   
    public static final double _AVFACTOR_MIN = 0.2;
   
    public long getLastRequestTime();
    public long getMaxRequestTime();
    public void resetMaxRequestTime();
   
    public double getMetricValue(String metricName, String mapiMethodName);
    public double getAvailabilityFactor();
    public double getAvailabilityFactor(String serv);

    public String[] getMetricsValueTrendDataGrammas(String metricName, String mapiMethodName, Long key);
    
    public void start();
    public void stop ();
    public boolean isAvailable();
    public boolean isServiceLocked();
    public void startControlling();
    public void  registerEvent(String methName, long dur, long timeev, double avFactor); 
    public void  registerService(String servName, double avFactor); 
    public String[] getMethods();  
    public String[] getServices();  
    public String[] getTrend (String meth); 
    public Double getAvailability(String service); 
    
}
