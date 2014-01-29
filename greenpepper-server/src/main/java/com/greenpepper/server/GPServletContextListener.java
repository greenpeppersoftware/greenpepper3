package com.greenpepper.server;

import java.net.URL;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.greenpepper.server.configuration.ServerConfiguration;
import com.greenpepper.server.database.hibernate.BootstrapData;
import com.greenpepper.server.database.hibernate.HibernateSessionService;
import com.greenpepper.server.license.Authorizer;
import com.greenpepper.server.license.DefaultAuthorizer;
import com.greenpepper.util.URIUtil;

public class GPServletContextListener implements ServletContextListener
{    
    private static String GREENPEPPER_CONFIG = "greenpepper-server.cfg.xml";
    
    public void contextInitialized(ServletContextEvent servletContextEvent)
    { 
        ServletContext ctx = servletContextEvent.getServletContext();
        ctx.log("******* Mounting up GreenPepper-Server");
        try
        {
            URL url = GPServletContextListener.class.getClassLoader().getResource(GREENPEPPER_CONFIG);
            Properties sProperties = ServerConfiguration.load(url).getProperties();
            injectAdditionalProperties(ctx, sProperties);
            
            HibernateSessionService service = new HibernateSessionService(sProperties);
            ctx.setAttribute(ServletContextKeys.SESSION_SERVICE, service);
            
            ctx.log("Boostrapping datas");
            new BootstrapData(service, sProperties).execute();
            
            Authorizer authorizer = new DefaultAuthorizer(service, sProperties);
            authorizer.initialize(GreenPepperServer.versionDate());
            ctx.setAttribute(ServletContextKeys.AUTHORIZER, authorizer);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }      
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        ServletContext ctx = servletContextEvent.getServletContext();
        HibernateSessionService service = (HibernateSessionService)ctx.getAttribute(ServletContextKeys.SESSION_SERVICE);
        if(service != null) service.close();
    }
    
    private void injectAdditionalProperties(ServletContext ctx, Properties sProperties)
    {
        String dialect = ctx.getInitParameter("hibernate.dialect");        
        if(dialect != null) sProperties.setProperty("hibernate.dialect", dialect);
        if(ctx.getRealPath("/") != null) 
        {
            sProperties.setProperty("baseUrl", URIUtil.decoded(ctx.getRealPath("/")));
        }
    }
}
