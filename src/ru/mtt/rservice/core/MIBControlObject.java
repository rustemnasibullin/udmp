package ru.mtt.rservice.core;

import org.snmp4j.PDU;

import ru.mtt.webapi.utils.XUtils;

/**
 *  Service control data object container  and MIB parser
 *
 *  @author rnasibullin@mtt.ru  Chief
 */

public class MIBControlObject {
    
    
    public static final int _SERVICESTARTED = 1;
    public static final int _SERVICESTOPED = 2;
    public static final int _SERVICEINTROUBLE = 3;
    public static final int _SERVICERELEIVED = 4;
    public static final int _SERVICEACTUALIZED = 5;
    
    String oid = null;
    int eventId = 0;
    String jmxHost = null;
    String jmxDefPort = "7703";
    int jmxPort = 7703;
    double avFactor = 0.0;
    
    public MIBControlObject() {

           super();

    }
    
    public void parse (Object o) {

           String po = null;
           XUtils.ilog ("log/snmpproc.log", "SNMP: " + o);
           String portX = System.getProperty("com.sun.management.jmxremote.port", jmxDefPort);
           this.setJmxPort (Integer.parseInt(portX));

           if (o instanceof String) {

               po = (String) o;

           } else {
           
               XUtils.ilog ("log/snmpproc.log", "SNMP: " +o.getClass().getName());

           }
        
            
           String [] tags = po.split("[<>]");
           boolean use_value = false;
           boolean use_ip = false;
           for (String c: tags) {
                
                if (c==null || c.length()==0) continue;
                 
                if (c.equals("value")) {

                    use_value = true;

                } else if (c.equals("agent-addr")) {

                    use_ip = true;

                } else {
                    
                    if (use_value) {
                         
                         use_value = false;
                         switch (c) {
                         case "start":
                             eventId =  _SERVICESTARTED;
                         break;
                         case "stop":
                             eventId =  _SERVICESTOPED;
                         break;
                         case "introuble":
                             eventId =  _SERVICEINTROUBLE;
                         break;
                         case "releived":
                             eventId =  _SERVICERELEIVED;
                         break;
                         }
                         
                    }

                    if (use_ip) {
                        use_ip = false;
                        oid = c;
                        this.setJmxHost(oid);
                    }
                     
                 }
               
           }
        
    }

    public void setAvFactor(double avFactor) {
        this.avFactor = avFactor;
    }

    public double getAvFactor() {
        return avFactor;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setJmxHost(String jmxHost) {
        this.jmxHost = jmxHost;
    }

    public String getJmxHost() {
           return jmxHost;
    }

    public void setJmxPort(int jmxPort) {
           this.jmxPort = jmxPort;
    }

    public int getJmxPort() {
           return jmxPort;
    }

    public String getOID () {
           return oid; 
    }
    
    public String toString() {
           return "ip:"+oid+",eventId:"+eventId;
    }
    
}
