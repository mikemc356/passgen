package com.systemwerx.common.web.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.systemwerx.common.web.service.InstallService;

import org.jboss.logging.Logger;

public class InstallServiceImpl implements InstallService {

    Logger log = Logger.getLogger(InstallServiceImpl.class);

    @Override
    public boolean processInstallActions(Connection conn, String schema) {
        //Connection conn = null;
        Statement stmt = null;
        
        // Check if we have tables currently
        try {
            stmt = conn.createStatement();
            stmt.executeQuery("select count(*) from "+schema+".app_user");
        } catch (SQLException e) {
            // If we fail then we need to create everything
            try {
                URL url = this.getClass().getClassLoader().getResource("sql-scripts/createdb.sql");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                String inputLine;
                StringBuffer sb = new StringBuffer();
                while ( (inputLine = br.readLine()) != null ) {
                    sb.append(inputLine);
                    sb.append("");
                    inputLine = inputLine.trim();

                    if ( inputLine.endsWith(";")) {
                        log.info("Processing SQL : "+sb.toString());
                        stmt.executeUpdate(sb.toString());
                        sb = new StringBuffer();
                    }
                }

            } catch (IOException | SQLException e1) {
                //throw e1;
                System.out.println(e1);
                log.error("Exception processing SQL statement : "+e1.getMessage());
            }
        } 
 
        return true;
    }
    
}