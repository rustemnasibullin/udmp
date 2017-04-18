package org.mtt.webapi.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.mtt.webapi.core.XDAOController;
import org.mtt.webapi.core.XSmartObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.dom.User;


public class OptDAO extends XDAOController  {
    

    public static final String _opt_log = "log/optdao.log"; 

    
    public static final String INITSYS_SQL =  
    "Create TABLE IF NOT EXISTS xbus.user (" +
    "  id int(11) NOT NULL AUTO_INCREMENT, "+
    "  username varchar(255) NOT NULL, "+
    "  auth_key varchar(32) NOT NULL, "+
    "  password_hash varchar(255) NOT NULL, "+
    "  password_reset_token varchar(255) NOT NULL DEFAULT '', "+
    "  email varchar(255) NOT NULL, "+
    "  reseller_id varchar(255) NOT NULL, "+
    "  role smallint(6) NOT NULL DEFAULT 10, "+
    "  status smallint(6) NOT NULL DEFAULT 10, "+
    "  created_at int(11) UNSIGNED NOT NULL, "+
    "  updated_at int(11) UNSIGNED NOT NULL, "+
    "  tech_function_access smallint(6) DEFAULT NULL, "+
    "  store_time int(11) DEFAULT NULL, "+
    "  PRIMARY KEY (id), "+
    "  UNIQUE INDEX user_auth_key (auth_key), "+
    "  UNIQUE INDEX user_username (username) "+
    ");";

    public static final String INITSYS_SQL_EXT =  
    "Create TABLE IF NOT EXISTS xbus.sys (" +
    "  id int(11) NOT NULL AUTO_INCREMENT, "+
    "  customerId int(11) UNSIGNED NOT NULL, "+
    "  auth_token varchar(255), "+
    "  systemId varchar(255) NOT NULL, "+
    "  active smallint(6) DEFAULT 1, "+
    "  update_time int(11) DEFAULT NULL, "+
    "  PRIMARY KEY (id) "+
    ");";

    public static final String INITSYS_SQL_BIZ =  
    "Create TABLE IF NOT EXISTS xbus.bizrules (" +
    "  ALIAS VARCHAR(120) NOT NULL, "+
    "  BODYOF TEXT NOT NULL, "+
    "  update_time DATETIME, "+
    "  PRIMARY KEY (ALIAS) "+
    ");";

    public static final String INITSYS_SQL_CONF =  
    "Create TABLE IF NOT EXISTS xbus.confoptions (" +
    "  ALIAS VARCHAR(120) NOT NULL, "+
    "  VALUE VARCHAR(120) NOT NULL, "+
    "  PRIMARY KEY (ALIAS) "+
    ");";

    
     public OptDAO() {
            super();
     }

     public static Logger logger = Logger.getLogger(OptDAO.class);

     @Override
     public void doConfig() {
     }

     private JdbcTemplate jdbcTemplate = null;
    
     
     public void setDataSource(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
     };
    
     public void init () {
         
              
            jdbcTemplate.execute(INITSYS_SQL);
            jdbcTemplate.execute(INITSYS_SQL_EXT);
            jdbcTemplate.execute(INITSYS_SQL_BIZ);
            jdbcTemplate.execute(INITSYS_SQL_CONF);

         
     }

     public void stop () {
         
     }
     
     public boolean updateBizRule(final String alias, final String bodyof) {
    
        boolean res = true;    
        String sql  = "update rc_search.bizrules set BODYOF =?,update_time=?  where  ALIAS=?";
                                            
        try {
        
        jdbcTemplate.update(sql, new PreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
            
           preparedStatement.setString (1,bodyof); 
           preparedStatement.setDate (2,(new java.sql.Date(System.currentTimeMillis()))); 
           preparedStatement.setString (3,alias); 
           
        }

        }
                       
        );
        } catch (Throwable ee) {
            
            res = false;
            XUtils.ilog (_opt_log, XUtils.info(ee)); 
        
        }
             
    
        return res;
           
    }; 



    public String getOption(final String alias) {
    
       String res = null;    
       String sql  = "select VALUE FROM rc_search.confoptions where  ALIAS=?";
                                           
       try {
       
       List<String> resV = jdbcTemplate.query(sql, new PreparedStatementSetter() {

       @Override
       public void setValues(PreparedStatement preparedStatement) throws SQLException {
           
          preparedStatement.setString (1,alias); 
          
       }

       },
                                   
                    new RowMapper<String>(){

                            @Override
                            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                                
                                String s  =  null;
                                s = resultSet.getString(1);                              
                                return s;

                            }

                    }
                                                    
                      
       );
           
           if (resV != null && resV.size()>0)   {
               res = resV.get(0);
           }
           
       } catch (Throwable ee) {
           
           res = null;
            XUtils.ilog (_opt_log, XUtils.info(ee)); 
       
       }
            
    
       return res;
          
    };


    public void setOption(final String alias, final String value) {
    
       boolean res = true;    
       String sql1  = "delete from rc_search.confoptions where  ALIAS=?";
       String sql2  = "insert into rc_search.confoptions (ALIAS, VALUE) values(?,?)";
                                           
       try {
       
       jdbcTemplate.update(sql1, new PreparedStatementSetter() {

       @Override
       public void setValues(PreparedStatement preparedStatement) throws SQLException {
           
          preparedStatement.setString (1,alias); 
          
       }

       }
                      
       );
 
       jdbcTemplate.update(sql2, new PreparedStatementSetter() {

       @Override
       public void setValues(PreparedStatement preparedStatement) throws SQLException {
               
           preparedStatement.setString (1,alias); 
           preparedStatement.setString (2,value); 
              
       }

       }
                          
       );
           
       } catch (Throwable ee) {

            XUtils.ilog (_opt_log, XUtils.info(ee)); 
       
       }
            
    
    };




    public Map<String, String>  readBizRules() {
    
       
       final Map<String, String> res = new HashMap<>();
       String sql  = "select * from rc_search.bizrules order by ALIAS";
                                           
       try {
       
       jdbcTemplate.query(sql, 
                          
                          
           new RowMapper<String>(){

                   @Override
                   public String mapRow(ResultSet resultSet, int i) throws SQLException {
                       
                       String s  =  null;
                       s = resultSet.getString("ALIAS"); 
                       String sb = resultSet.getString("BODYOF"); 
                       res.put (s, sb);
                       return s;

                   }

           }
                      
       );
       } catch (Throwable ee) {

            XUtils.ilog (_opt_log, XUtils.info(ee)); 
       
       }
            
    
       return res;
          
    };
     

    public boolean insertBizRule(String alias, String bodyof) {
    
        boolean res = true;    
        String sql  = "insert into rc_search.bizrules(BODYOF,update_time,ALIAS) values (?,?,?)";
                                                   
        try {
        
        jdbcTemplate.update(sql, new PreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
            
           preparedStatement.setString (1,bodyof); 
           preparedStatement.setDate (2,(new java.sql.Date(System.currentTimeMillis()))); 
           preparedStatement.setString (3,alias); 
           
        }


        }
                       
        );
        } catch (Throwable ee) {
            
          res = false;
            XUtils.ilog (_opt_log, XUtils.info(ee)); 
        
        }
             
    
        return res;
           
    }; 
     
    public boolean removeBizRule(final String alias) {
    
        boolean res = true;    
        String sql  = "delete from rc_search.bizrules where ALIAS = ?";
                                                   
        try {
        
        jdbcTemplate.update(sql, new PreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
               preparedStatement.setString (1,alias); 
        }


        }
                       
        );
        } catch (Throwable ee) {
            
          res = false;
            XUtils.ilog (_opt_log, XUtils.info(ee)); 
        
        }
             
    
        return res;
           
    }; 
     


     
    public boolean deactivateAuthToken(String sys, Long  customerId) {
    
        boolean res = true;    
        String sql = "update rc_search.sys set active=0 where customerId=? and systemId=?";
        
        try {
        jdbcTemplate.update(sql, new PreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
           preparedStatement.setLong (1,customerId); 
           preparedStatement.setString (2,sys); 
           
        }


        }
                       
        );
        } catch (Throwable ee) {
            
          res = true;  
        
        }
             
    
        return res;
           
     }; 

     
     public boolean setAuthToken(String sys, Long  customerId, String authToken) {
     
         boolean res = true;    
         String sql  = "insert into rc_search.sys(customerId,systemId,auth_token,update_time) values (?,?,?,?)";
         deactivateAuthToken(sys, customerId);
                                             
         try {
         jdbcTemplate.update(sql, new PreparedStatementSetter() {

         @Override
         public void setValues(PreparedStatement preparedStatement) throws SQLException {
             
            preparedStatement.setLong (1,customerId); 
            preparedStatement.setString (2,sys); 
            if (authToken != null) {
            preparedStatement.setString (3,authToken); 
            } else {
            preparedStatement.setNull (3, Types.VARCHAR); 
            }

            long v = (long)(System.currentTimeMillis()/1000.0); 
            preparedStatement.setLong (4, v); 
            
         }


         }
                        
         );
         } catch (Throwable ee) {
             
           res = true;  
         
         }
              
     
         return res;
            
     }; 
      
      
     public String getAuthToken(String sys, Long customerId) {
         String token = null;
         List<String> res = null;
         String sql = "SELECT auth_token from rc_search.sys s where s.customerId = ? and s.systemId = ? and active=1";
         res = jdbcTemplate.query(sql, new PreparedStatementSetter() {

         @Override
         public void setValues(PreparedStatement preparedStatement) throws SQLException {
            preparedStatement.setLong (1,customerId); 
            preparedStatement.setString (2,sys); 
         }


         },
                        
         new RowMapper<String>(){

                 @Override
                 public String mapRow(ResultSet resultSet, int i) throws SQLException {
                     
                     String s  =  null;
                     s = resultSet.getString(1);                              
                     return s;

                 }

         }
                        
         );
         
         if (res != null && res.size()>0) {
             token = res.get(0);
         }
         
         return token;
         
     };
             
     
     public  XSmartObject findUser (String login, String psw64) {
        
         XSmartObject v = null;
         List<XSmartObject> res = new  ArrayList<>();
         String sql = "SELECT * from rc_search.user u where u.username = ? and u.password_hash = ?";
         res = jdbcTemplate.query(sql, new PreparedStatementSetter() {

         @Override
         public void setValues(PreparedStatement preparedStatement) throws SQLException {
            preparedStatement.setString (1,login); 
            preparedStatement.setString (2,psw64); 
         }

         },
                        
         new RowMapper<XSmartObject>(){

                 @Override
                 public XSmartObject mapRow(ResultSet resultSet, int i) throws SQLException {
                     
                     User oo =  null;
                     oo = new User();
                     
                     try {

                       oo.populate(resultSet);                              
                     
                     } catch (WAPIException ee) {
                       oo = null;      
                     }
                     
                     return oo;

                 }

             }
                        
         );
         
         XSmartObject resx = null;
         if (res.size() >0) {
             resx = res.get(0);
         }
         
         return resx;
         
       
     }
     
     
     public  void addUser (String recellerId, String login, String psw64, int  storeTime, String authKey) {
         
         
       
         
        jdbcTemplate.update(new  PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                            "insert into rc_search.user(username, password_hash, store_time, reseller_id, auth_key, email,created_at,updated_at) values (?, ?, ?, ?, ?, ?, ?, ?)",
                            new String[]{"id"});

                    int index = 1;
                    int t = (int)(System.currentTimeMillis()/1000);
                    String email = login+"@mtt.ru";
                    ps.setString(index++, login);
                    ps.setString(index++, psw64);
                    ps.setInt(index++, storeTime);
                    ps.setString(index++, recellerId);
                    ps.setString(index++, authKey);
                    ps.setString(index++, email);
                    ps.setInt(index++, t);
                    ps.setInt(index++, t);
                    return ps;
                }
        }
        );
         
     }

     public int getResellerStoreTime (String recellerId) {
         
   
        String sql = "SELECT * from rc_search.user u where u.reseller_id = ?";


        
        List<Integer> res = jdbcTemplate.query(sql, new PreparedStatementSetter() {
          

        @Override
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
           preparedStatement.setString (1,recellerId); 
        }


        },
                       
                new RowMapper<Integer>(){


                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    
                    Integer oo =  null;
                    oo = resultSet.getInt("store_time");
                    return oo;

                }

            }
                       
                       );

        int resx = 0;
        if (res.size() >0) {
            resx = res.get(0);
        }
        
        return resx;

               
    }          
 
    public List<XSmartObject> getAllUsers(Integer recellerId) {
        
        List<XSmartObject> res = new  ArrayList<>();
        String sql = "SELECT * from rc_search.user where reseller_id=?";


        res = jdbcTemplate.query(sql, new PreparedStatementSetter() {


    @Override
    public void setValues(PreparedStatement preparedStatement) throws SQLException {
           preparedStatement.setInt (1,recellerId); 
    }


    },
                       
                new RowMapper<XSmartObject>(){


                @Override
                public XSmartObject mapRow(ResultSet resultSet, int i) throws SQLException {
                    
                    User oo =  null;
                        
                    oo = new User();
                    
                    try {

                      oo.populate(resultSet);                              
                    
                    } catch (WAPIException ee) {
                      oo = null;      
                    }
                    
                    return oo;

                }

            }
                       
                       );

        return res;
    
    }
    

}
