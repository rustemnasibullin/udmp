package org.mtt.webapi.storage;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.mtt.webapi.utils.JDBCUtils;
import org.mtt.webapi.utils.XUtils;

public class StorageDAO {
    
    protected JdbcTemplate jdbcTemplate;
    protected JDBCUtils jdbcUtils = null;

    
    public StorageDAO() {
        super();
    }
    
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
    
    
    public void init() {}

    public void stop() {}
    
    
    
}
