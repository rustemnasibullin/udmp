package org.mtt.webapi.dom;

import java.sql.Connection;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.mtt.webapi.core.IConstants;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.storage.StorageDAO;
import org.mtt.webapi.utils.JDBCUtils;
import org.mtt.webapi.utils.XUtils;

public class LOG  extends XSmartObject implements IConstants {
   
    String path = null; 
    StringBuilder content = new StringBuilder();
    
    protected JdbcTemplate jdbcTemplate;
    protected JDBCUtils jdbcUtils = null;

    public void init() {}

    public void stop() {}
    
    public void setDataSource(DataSource dataSource) {
           this.jdbcTemplate = new JdbcTemplate(dataSource);
           try {
           
             Connection conn = dataSource.getConnection();
             conn.close();
           
           } catch (Throwable ee) {
            XUtils.plog("log/dao.log", XUtils.info (ee));
           }
           jdbcUtils = new JDBCUtils(dataSource);
    };
    
    @Override
    public void setFieldByName(String nm, Object V) throws WAPIException {
        
           if (nm.equals ("path")) {
               path = (String) V; 
           }

    }

    public void info(Object data){
           if (path != null)
            XUtils.ilog (path, String.valueOf(data));
           int nm = content.length();
           String x = String.valueOf (data)+"\n";
           if (nm+x.length() > SMAX) {
           content.delete(0,nm);            
           }
           content.append (data+"\n");
            
    }

    @Override
    public Object getFieldByName(String nm) throws WAPIException {
        
           if (nm.equals ("NAME")) {
               return "LOG"; 
           }
           return null;
           
    }
    
    
    public void clear () {
           content = null;
           content = new StringBuilder();
    }
    
    public String getContent() {
           return content.toString();
    }
    
    public LOG() {
        super();
 
    }
    
    
    public int getLength () {
           return content.length();
    }
    
    public static void main (String[] x) {
           
        LOG l = new LOG ();   
           
           
           
    }
    
}
