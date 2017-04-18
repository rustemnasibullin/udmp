package org.mtt.webapi.dom;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Date;

import org.mtt.webapi.core.IJDBCPopulable;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XSmartObject;

public class User  extends XSmartObject   implements IJDBCPopulable  {


    int id;
    String username;
    String auth_key;
    String password_hash;
    String password_reset_token;
    String email;
    int reseller_id;
    int role;
    int status;
    Date created_at;
    Date updated_at;
    int tech_function_access;
    int store_time;
    


    public User() {
        super();
    }

    @Override
    public void setFieldByName(String string, Object object) throws WAPIException {
        // TODO Implement this method

    }

    @Override
    public Object getFieldByName(String string) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void populate(ResultSet resultSet) throws WAPIException {

        try {
        id = resultSet.getInt ("id") ;
        username = resultSet.getString ("username");
        auth_key  = resultSet.getString ("auth_key");
        password_hash = resultSet.getString ("password_hash");
        password_reset_token = resultSet.getString ("password_reset_token");
        email = resultSet.getString ("email");
        reseller_id = resultSet.getInt ("reseller_id");
        role = resultSet.getInt ("role");
        status = resultSet.getInt ("status");
        created_at = resultSet.getDate ("created_at");
        updated_at= resultSet.getDate ("updated_at");
        tech_function_access = resultSet.getInt ("tech_function_access");
        store_time = resultSet.getInt ("store_time");
        } catch (SQLException ee) {
            
        }

/*
         id int(11) NOT NULL AUTO_INCREMENT,
         username varchar(255) NOT NULL,
         auth_key varchar(32) NOT NULL,
         password_hash varchar(255) NOT NULL,
         password_reset_token varchar(255) NOT NULL DEFAULT '',
         email varchar(255) NOT NULL,
         reseller_id varchar(255) NOT NULL,
         role smallint(6) NOT NULL DEFAULT 10,
         status smallint(6) NOT NULL DEFAULT 10,
         created_at int(11) UNSIGNED NOT NULL,
         updated_at int(11) UNSIGNED NOT NULL,
         tech_function_access smallint(6) DEFAULT NULL,
         store_time int(11) DEFAULT NULL,
*/
    }


}
