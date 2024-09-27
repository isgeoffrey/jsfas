package jsfas.common.jasper;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

public class JasperReportsConfigurer implements JasperReportsConfig, ServletContextAware {

    private ServletContext servletContext;
    
    private String templatePath;
    
    @Override
    public InputStream getReportResourceAsStream(String templateName) {
        return servletContext.getResourceAsStream(templatePath + "/" + templateName);
    }

    @Override
    public void setTemplatePath(String templatePath) {
        // TODO Auto-generated method stub
        this.templatePath = templatePath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        // TODO Auto-generated method stub
        this.servletContext = servletContext;
        
    }
}
