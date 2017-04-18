package ru.mtt.webapi.xresource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;


import org.apache.camel.Exchange;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ru.mtt.webapi.controller.IJSONRPCControlObject;
import ru.mtt.webapi.controller.JSONRPCControlObject;
import ru.mtt.webapi.controller.JSONRPCControlObjectExt;
import ru.mtt.webapi.controller.XWebApiController;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.IMemCache;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.core.XMap;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dom.BizRuleInfo;
import ru.mtt.webapi.dom.SimpleXSmartObject;
import ru.mtt.webapi.utils.MQClient;
import ru.mtt.webapi.utils.OptDAO;
import ru.mtt.webapi.utils.XUtils;

public class XResource  extends XWebApiController implements IConstants {
    
    AtomicInteger version =  new AtomicInteger  (0);
    NotificationProxy notificationProxy = null; 
    final Map<String, String> resourceX = new ConcurrentHashMap <String, String> ();
    OptDAO sysdao = null;
    
    public XResource() {
           super();
    }

    public void setSysdao(OptDAO sysdao) {
        this.sysdao = sysdao;
    }

    public OptDAO getSysdao() {
        return sysdao;
    }

    public void setNotificationProxy(NotificationProxy notificationProxy) {
        this.notificationProxy = notificationProxy;
    }

    public NotificationProxy getNotificationProxy() {
        return notificationProxy;
    }

    boolean useDBSTORAGE = false;

    public void start() {
        
        boolean upload = true;
        if (useDBSTORAGE) {
        String bizLoaded = sysdao.getOption("BIZRULES");
        
        if (bizLoaded != null && "ON".equals(bizLoaded)) {
            
            Map<String, String> bizrules = sysdao.readBizRules();
            resourceX.putAll(bizrules);
            upload = false;  
        } 
        
        } 
        
        if (upload) {
        
        loadGroovyFiles ();
        loadScalaFiles ();
        loadRubyFiles ();
        loadPythonFiles ();
        sysdao.setOption("BIZRULES", "ON");    
        
        }    
            
    }

    public void stop() {
        
    }

    public void loadGroovyFiles () {
    
        loadXFiles ("groovy");
    
    }

    public void loadScalaFiles () {
        
        loadXFiles ("scala");
        
    };
    
    public void loadRubyFiles () {
        
        loadXFiles ("ruby");
        
    };
    
    public void loadPythonFiles () {
        
        loadXFiles ("python");
        
    };

 
    public void notifySubscribers(String path) {
    
        MQClient uMQ = notificationProxy.createMQClientForService(_XNotification);
        JSONRPCControlObject cntr = new JSONRPCControlObject ();
        cntr.setMethod("xresourceUpdated");
        cntr.setJsonrpc("2.0");
        cntr.setId("1");
        cntr.setVersion("1.0");
        cntr.setParams(new String[]{path});
        Object resp = uMQ.sendMessage(cntr, true);
        
        try {
        
        Object obj =  null;
        
            if (resp instanceof XSmartObject) {
        
                obj = ((XSmartObject) resp).getFieldByName(_ERROR);
                if (obj == null) {

                    String jsonrpcResponse = (String) ((XSmartObject) resp).getFieldByName("VALUE");  
                    cntr.setJsonrpc(jsonrpcResponse);
                    Object robj = cntr.getResult();
                         
                }

            }           
            
        } catch (Throwable ee) {
        }
    
    };

    public void loadXFiles (String x) {
           
           File f = new File("./"+x);
           String[] fl = f.list();
           for (String fs: fl) {
               
                if (fs.endsWith("."+x)) {
                    
                    try {
                        
                        File fx =  new File("./"+x+"/"+fs);
                        if (fx.isFile()) {  
                            String code =  FileUtils.readFileToString(fx, "utf-8");
                            String  nm = fx.getName();
                            int inx = nm.indexOf("."+x);
                            String aName = nm.substring(0, inx); 
                            String key = x+"://ru.mtt.icloud.bizrules."+aName;
                            code = code.replace("\"","'");
                            resourceX.put (key, code);
                            sysdao.removeBizRule(key);
                            sysdao.insertBizRule(key, code);
                            BizRuleInfo bzrInfo = new BizRuleInfo (key, code);
                            sysCache.insert(bzrInfo);
                        }
                        
                    } catch (Throwable ee) {
                      ee.printStackTrace();      
                    }
                   
                }
               
//                
           }
    }


    @Override
    public void loadAliases() {

        XResourceAction a = new XResourceAction();
        a.setOwner(this);
        actions.put ("getResourceByPath", a);
        actions.put ("updateResourceByPath", a);
        actions.put ("getResourceByPathAsList", a);
        actions.put ("getResourceByPathAsMAP", a);
        actions.put ("getFolderByPathAsMAP", a);
        actions.put ("getFolderByPathAsList", a);

    }

    @Override
    public synchronized void process(Exchange exchange) throws Exception {
        
        try {
        
        version.getAndIncrement();
        log.info ("XResource Mock Up component");
        String qs = exchange.getIn().getBody(String.class);
        Map<String, Object> hdr = exchange.getIn().getHeaders(); 
        org.apache.camel.component.http.HttpMessage  msg = (org.apache.camel.component.http.HttpMessage) exchange.getIn();
        HttpServletRequest req = msg.getRequest();
        String method = req.getMethod(); 
        String uri = req.getContextPath();
        XUtils.plog("log/_xres.log", uri);    
        if (method.toUpperCase().equals("GET")) {    
        
            String ruleId = req.getParameter("bizruleid");
            String code = "";
            if (ruleId != null) {
                
                if ("null".equals(ruleId)) {
                    ruleId = null;
                    code = "";
                } else {
                    String newCode = req.getParameter("RULECODE");
                    String act = req.getParameter("ACT");
                    if (newCode != null && newCode.trim().length()>0) {
                        if ("update".equals(act)) {   
                        code = newCode; 
                        resourceX.put(ruleId, code);
                        sysdao.updateBizRule(ruleId, code);
                        XUtils.plog("log/xres1.log", resourceX.toString());
                        notifySubscribers (ruleId);
                        }
                    }
                    code = resourceX.get (ruleId);
                }
                    
            }
        
            Set<String> ks = resourceX.keySet();
            String z = "";
            String sel = "";
            for (String x:ks) {
                 sel="";
                 if (x.equals(ruleId)) sel = "selected";
                 z+="\n<option value='"+x+"' "+sel+">"+x+"</option>";
            }
            String javascript = "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.2.6/jquery.min.js\">"+
                                "</script>\n";   
            javascript += "<script language=\"JavaScript\">\n" +
                "" +
                "" +
                "" +
                "function loadCode() {\n" +
                "" +
                "" +
                "document.forms[\"regitem\"].RULECODE.value=\"\";\n"+          
                "document.forms[\"regitem\"].submit();\n" +
                "" +
                "" +
                "};\n" +
                "function updateCode() {\n" +
                "document.forms[\"regitem\"].ACT.value='update';\n" +
                "document.forms[\"regitem\"].submit();\n" +
                "" +
                "" +
                "" +
                "};\n" +
                "function updateCode2() {\n" +
                "var bizruleid = document.forms[\"regitem\"].bizruleid.value;\n" +
                "var data = document.forms[\"regitem\"].RULECODE.value;\n" +
                          
                "var dataText = bizruleid+\"\\n\"+data;\n"+
                          
                          
                          
                "var dataJSON = \"{'id': '1','jsonrpc': '2.0','method': 'updateResourceByPath',"+
                "'params':{"+ 
                "'ruleid':'\"+bizruleid+\"',"+
                "'data':'\"+data+\"'}}\";\n\n" +
 
                "jQuery.ajax({\n"+   
                "    url: \"http://172.16.104.5/xresource\",   \n"+   
                "    data : dataText,                    \n"+   
                "    type : \"PUT\",                    \n"+   
                "    dataType : \"text\",                    \n"+   
                "    success: function (data, textStatus) { \n"+   
                "    } \n"+  
                "}); \n"+          
                "" +
                "};\n" +
                "" +
                "</script>";    
                
            String rules = "<select name='bizruleid' onchange='loadCode();'>" +
                "<option value='null'>Choose&nbsp;RuleId</option>" +
                z  +
                "" +
                "" +
                "" +
                "" +
                          "</select>";
            String html = "<html>\n"+javascript+"\n<body>\n" +
                          "" +
                          "<CENTER>" +
                          "<p> <H2>XResource BizRules Storage ("+version+"). </H2></p>" +
                          "" +
                          "<form name='regitem' >" +
                          "<input name=\"ACT\" type=\"hidden\" value=\"select\" />" +
                          "<table>" +
                          "<tr><td>Select&nbsp;BizRule&nbsp;for&nbsp;update&nbsp;one:</td><td>" + rules + "</td></tr>"+
                          "<tr><td colspan=2><textarea rows=40 cols=80 name='RULECODE'>" + code + "</textarea> </td></tr>"+
                          "" +
                          "" +
                          "<tr><td colspan=2>Update&nbsp;Original&nbsp;Rules:<input name=\"reload\" type=\"checkbox\" value=\"0\" /> </td></tr>"+
                          "" +
                          "<tr><td colspan=2><A href='javascript:updateCode2()'>Update</A></td></tr>"+
                          "</table>" +
                          "</form>" +
                          "</CENTER> </body>   </html>";
        
            exchange.getOut().setHeader("Content-Type", "text/html");
            exchange.getOut().setHeader("Content-Length", html.getBytes().length);
            exchange.getOut().setHeader("Content-Encoding", "UTF-8");
            exchange.getOut().setHeader("exitcode", -1);            
            exchange.getOut().setBody(html);
            
        } else if (method.toUpperCase().equals("PUT")) {  
            
            int xs = qs.indexOf("\n");
            String path = qs.substring(0, xs);
            String body = qs.substring(xs+1);
            XUtils.plog("log/_xresource.log", path);   
            XUtils.plog("log/_xresource.log", body);   
            String m = "updateResourceByPath";
            XAction a = actions.get(m);
            a.execute(m, new String[]{path, body});
            
        } else {      
            
        String path = req.getRequestURI();          
        log.info ("Test Info1:  " + qs);
        log.info ("Test Info2:  " + path);
            
        IJSONRPCControlObject c = XUtils.toJSONRPCControl(qs);
        String  m = c.getMethod();    
        Object  p = c.getParamsList();    
        XAction a = actions.get(m);
           
        XUtils.plog("log/_xresource.log", qs);   
        XUtils.plog("log/_xresource.log", m);   
            
        String err_text  = null;
        int exit_code = 0;

        JSONRPCControlObject o = new JSONRPCControlObject();
        o.setVersion("1.0");    
        o.setJsonrpc("2.0");
        o.setId(c.getId());

        if (a != null) {    
            
            Object res =  null;
            if (p instanceof Map) {    
            
                Map mp = (Map)p; 
                res =  a.execute(m, mp);     
            
            } else  {
                
                String[] pp = (String[])p; 
                res =  a.execute(m, pp);     
                
            }

        o.setResult(res);
        
        }  else  {
            
        o.setError("Method name is not Valid.");   
            
        }

        log.info (resourceX);
        exchange.getOut().setHeader("exitcode", exit_code);   
        exchange.getOut().setBody(o.toString());

        }
        
        } catch (Throwable ee) {

        String tss = XUtils.info(ee);
        XUtils.ilog("log/XResourceMockUP.log", tss);
        String err_mess = ee.getClass().getName()+":"+ee.getMessage();
        log.info (err_mess);  
        exchange.getOut().setHeader("Content-Type", "application/json");
        exchange.getOut().setHeader("Content-Length", err_mess.getBytes().length);
        exchange.getOut().setHeader("Content-Encoding", "UTF-8");
        exchange.getOut().setHeader("exitcode", -1);            
        exchange.getOut().setBody(err_mess);

        }
            
        
        
    }


    class XResourceAction extends XAction implements IXResource {
    
        @Override
        public String[] getActionList() {
               return new String[]{"getResourceByPath","updateResourceByPath","getResourceByPathAsList","getResourceByPathAsMAP","getFolderByPathAsList","getFolderByPathAsMAP"};
        }

        @Override
        public synchronized Object execute(String act, String[] params) {
            
            
            Object out = super.execute(act, params);
            XSmartObject o = (XSmartObject ) out;

            try {
               
                if (o != null) {
                Integer iErr = (Integer) o.getFieldByName(XSmartObject._ERROR);
                if (iErr != null && iErr.intValue()>0) {
                    return o;      
                }
                }
                
                if ("getResourceByPath".equals(act)) {
                    
                    String data = getResourceByPath(params[0]);
                    
                    log.info ("data: "+data);
                    
                    
                    o = new SimpleXSmartObject ("value", data);
                    
                }
                
                if ("updateResourceByPath".equals(act)) {

                    updateResourceByPath(params[0], params[1]);
                    o = new SimpleXSmartObject ("status", "ok");
                    
                }
                
                if ("getResourceByPathAsList".equals(act)) {
                    
                    List<String> dList = getResourceByPathAsList(params[0]);
                    
                    List <XSmartObject> objList = new ArrayList<>();
                    for (String x: dList) {
                        objList.add (new SimpleXSmartObject ("v", x));
                    }
                    
                    o = new XCollection (objList);
                    
                }
                
                if ("getResourceByPathAsMAP".equals(act)) {

                    Map<String,String> dMap = getResourceByPathAsMAP (params[0]);
                    XMap ms = new XMap();
                    Set<Map.Entry<String, String>> XS =  dMap.entrySet();
                    Iterator<Map.Entry<String, String>> it = XS.iterator();
                    while (it.hasNext()) {
                            Map.Entry<String, String> v = it.next();
                            ms.setFieldByName(v.getKey(), v.getValue());
                        
                    }
                    o = ms;
                    
                } 
  
                if ("getFolderByPathAsList".equals(act)) {
                    
                    List<String> dList = getFolderByPathAsList(params[0]);
                    
                    List <XSmartObject> objList = new ArrayList<>();
                    for (String x: dList) {
                        objList.add (new SimpleXSmartObject ("v", x));
                    }
                    
                    o = new XCollection (objList);
                    
                }
                
                if ("getFolderByPathAsMAP".equals(act)) {

                    Map<String,String> dMap = getFolderByPathAsMAP (params[0]);
                    XMap ms = new XMap();
                    Set<Map.Entry<String, String>> XS =  dMap.entrySet();
                    Iterator<Map.Entry<String, String>> it = XS.iterator();
                    while (it.hasNext()) {
                            Map.Entry<String, String> v = it.next();
                            ms.setFieldByName(v.getKey(), v.getValue());
                        
                    }
                    o = ms;
                    
                } 
                          
            } catch (WAPIException ee) {
                
              o = new SimpleXSmartObject ("status", "failed");  
              ee.printStackTrace();  
              
            }
            
            
            return o;
            
                
        }
        
        
        @Override
        public String getResourceByPath(String path) {
            
            String v = (String) resourceX.get(path);
            XUtils.plog("log/xres2.log", resourceX.toString());
            return v;
        }

        @Override
        public List<String> getResourceByPathAsList(String path) {
            String v = (String) resourceX.get(path);
            List<String> xl = null;
            if (v != null) {
                String[] vs = v.split("\n");
                xl = new ArrayList<String>(Arrays.asList(vs));
            }
            return xl;
        }

        @Override
        public Map<String, String> getResourceByPathAsMAP(String path) {
               String v = (String) resourceX.get(path);
               
               
               
               Map<String, String> xl = null;
               if (v != null) {
                   String[] vs = v.split("\n");
                
                   xl = new HashMap<String, String> ();
                   for (String s: vs) {
                    
                        int xs = s.indexOf("=");
                        if (xs>0) {
                            xl.put (s.substring(0, xs), s.substring(xs+1));                              
                        }
                    
                   }
                
               }
               return xl;
        }

        @Override
        public List<String> getFolderByPathAsList(String path) {
               Set <String> ks = resourceX.keySet();
               List<String>  xl = new ArrayList<String> ();
               for (String k: ks) {
                    if (k.startsWith(path)) {
                        String v = resourceX.get (k);
                        xl.add (v);  
                    }
               }
               return xl;
        }

        @Override
        public Map<String, String> getFolderByPathAsMAP(String path) {
               Map<String, String> xl = new HashMap<String, String>();
               
               log.info ("---   PathLoad:  "+path);
               
               Set <String> ks = resourceX.keySet();
               for (String k: ks) {
                    log.info ("Key: "+k);
                    if (k.startsWith(path)) {
                        String v = resourceX.get (k);
                        log.info (k+"="+v);
                        xl.put(k,v);  
                    }
               }
               return xl;
        }



        @Override
        public void updateResourceByPath(String path, String data) throws WAPIException {
               resourceX.put(path, data);
               BizRuleInfo bzrInfo = new BizRuleInfo (path, data);
               sysdao.updateBizRule(path, data);
               sysCache.insert(bzrInfo);
               notifySubscribers(path);
        }



    }




    public static void main (String[] a) {
        
           XResource x = new XResource  ();
           x.loadGroovyFiles();
        
    }



}
