package de.radiohacks.frinmeba.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MyConnection implements Serializable {

    private static final long serialVersionUID = -5775145889024550232L;
    private static final Logger LOGGER = Logger.getLogger(MyConnection.class.getName());

    private Connection con = null;

    public MyConnection() {
        super();
    }

    public Connection getConnection() {
        Properties p = new Properties();
        
        String resourceName = "database.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        p = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            p.load(resourceStream);
        } catch (IOException e) {
        	LOGGER.error("can not read ressourcen properties file");
            LOGGER.error(e);
        }

        try {
            Class.forName(p.getProperty("driver"));
        } catch (ClassNotFoundException e) {
        	LOGGER.error("can not find driver " + p.getProperty("driver"));
            LOGGER.error(e);
        }

        try {
            con = DriverManager.getConnection(
                    p.getProperty("url"), 
                    p.getProperty("username"),
                    p.getProperty("password"));
        } catch (SQLException e) {
        	LOGGER.error("can not connect database : " + p.getProperty("url"));
            LOGGER.error(e);
        }

        return this.con;
    }

    public void closeConnection() {
        try {
            if (!con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
        	LOGGER.error("can close connection");
            LOGGER.error(e);
        }
    }

}
