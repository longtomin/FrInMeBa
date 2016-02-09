package de.radiohacks.frinmeba.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.PropertyConfigurator;

/**
 * Servlet implementation class Log4jInitServlet
 */
@WebServlet("/Log4jInitServlet")
public class Log4jInitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Log4jInitServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        PropertyConfigurator.configure(loader.getResource("log4j.properties"));
        
        Properties p = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream("log4j.properties")) {
            p.load(resourceStream);
        } catch (IOException e) {
        	System.err.println("can not read ressourcen properties file");
            e.printStackTrace();
        }
        
        System.err.println("write logs to " + p.getProperty("log4j.appender.file.File"));
        
        super.init(config);
    }

}
