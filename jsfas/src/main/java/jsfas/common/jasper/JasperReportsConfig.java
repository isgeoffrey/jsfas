package jsfas.common.jasper;

import java.io.InputStream;

public interface JasperReportsConfig {

    public void setTemplatePath(String templatePath);
    
    public InputStream getReportResourceAsStream(String templateName);
}
