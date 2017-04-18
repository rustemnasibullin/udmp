package ru.mtt.rservice.core;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import ru.mtt.webapi.utils.XUtils;


/**
 *  SNMP Message processing bean
 *
 *  @author rnasibullin@mtt.ru  Chief
 */


public class SNMPProcessor implements Processor
{
    
    static final MIBControlObject  co = new MIBControlObject ();

    
    public SNMPProcessor() {
        super();
    }


    @Override
    public void process(Exchange exchange) throws Exception {
           org.apache.camel.component.http.HttpMessage  msg = (org.apache.camel.component.http.HttpMessage) exchange.getIn();
           HttpServletRequest req = msg.getRequest();
           String path = req.getRequestURI(); 
           String[] ps = path.split("[/]");
           XUtils.ilog ("log/snmpproc.log", "SNMP: " +path);
           String data ="<agent-addr><"+ps[2]+">"+"<value><"+ps[3]+">";
           process2(data);
    }


    public Object process2(Object o) {
        
           co.parse(o);
           
           XUtils.plog ("log/snmpproc.log", "SNMP: " +o + "/" + co);
           int evId = co.getEventId();
           switch (evId) {
           case MIBControlObject._SERVICESTARTED:
               MAPIServiceFarmHandler.getInstance().startScanService(co.getOID(), co.getJmxHost(), co.getJmxPort());
           break;    
           case MIBControlObject._SERVICESTOPED:
               MAPIServiceFarmHandler.getInstance().stopScanService(co.getOID());
           break;    
           case MIBControlObject._SERVICEINTROUBLE:
               MAPIServiceFarmHandler.getInstance().suspendPromoteUsageService(co.getOID());
           break;    
           case MIBControlObject._SERVICERELEIVED:
               MAPIServiceFarmHandler.getInstance().startScanService(co.getOID(), co.getJmxHost(), co.getJmxPort());
           break;    
           }

           return null;
        
    }
    
    
}
