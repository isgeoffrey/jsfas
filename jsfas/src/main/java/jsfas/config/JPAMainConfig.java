package jsfas.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jsfas.common.PasswordProtector;
import jsfas.common.constants.AppConstants;
import jsfas.db.CommonJavaRepositoryImpl;


/**
 * @author iseric
 * @since 12/5/2016
 * @see Class for db connection pool setup
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = {"jsfas.db.main", "jsfas.db.rbac", "jsfas.security.realm"}, //all repositories under this basePackages will use this connection pool 
	entityManagerFactoryRef = "entityManagerFactoryMain", //this name must be unique throughout all java application within same tomcat, format : entityManagerFactoryXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	transactionManagerRef = "transactionManagerMain", //this name must be unique throughout all java application within same tomcat , format : transactionManagerXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	repositoryBaseClass = CommonJavaRepositoryImpl.class
)
public class JPAMainConfig {
	
	@Autowired
	private Environment env;
	
	@Autowired
	PasswordProtector pp;
	
	@Bean
	@Primary //Only Main DB is needed to set Primary
	//this name must be unique throughout all java application within same tomcat, format : entityManagerFactoryXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryMain() { 
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSourceMain());
		em.setPackagesToScan(new String[] { "jsfas.db.main", "jsfas.db.rbac", "jsfas.security.realm" }); //all repositories under this basePackages will use this connection pool 
 
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("javax.persistence.lock.timeout", AppConstants.LOCK_TIMEOUT);
		props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
		em.setJpaPropertyMap(props);
		   
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		return em;
	}
 
	@Bean
	//@Primary //Only Main DB is needed to set Primary
	//this name must be unique throughout all java application within same tomcat, format : sessionFactoryXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	public LocalSessionFactoryBean sessionFactoryMain() { 
		Properties prop = new Properties();
		prop.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
		
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSourceMain());
		sessionFactory.setPackagesToScan(new String[] { "jsfas.db.main", "jsfas.db.rbac", "jsfas.security.realm" }); //all repositories under this basePackages will use this connection pool 
		sessionFactory.setHibernateProperties(prop);
		return sessionFactory;
	}
   
	@Bean
	@Primary //Only Main DB is needed to set Primary
	//function name must be unique throughout all java application within same tomcat, format : dataSourceXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	public DataSource dataSourceMain() {
	//public Log4jdbcProxyDataSource dataSourceJstpMain() { //log4jdbc, for pre-dev only
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty(AppConstants.DB_DRIVER));
		dataSource.setUrl(env.getProperty(AppConstants.MAIN_DB_URL));
		dataSource.setUsername(env.getProperty(AppConstants.MAIN_DB_USERNAME));
		dataSource.setPassword(pp.decrypt(env.getProperty(AppConstants.MAIN_DB_PASSWORD)));
//		dataSource.setPassword(env.getProperty(AppConstants.MAIN_DB_PASSWORD));
		dataSource.setMaxTotal(Integer.parseInt(env.getProperty(AppConstants.DB_MAX_ACTIVE)));
		dataSource.setMaxIdle(Integer.parseInt(env.getProperty(AppConstants.DB_MAX_IDLE)));
		dataSource.setMaxWaitMillis(Integer.parseInt(env.getProperty(AppConstants.DB_MAX_WAIT)));
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");
		return dataSource;
		//return new Log4jdbcProxyDataSource(dataSource); //log4jdbc, for pre-dev only
	}
   
	@Bean
	@Primary //Only Main DB is needed to set Primary
	//function name must be unique throughout all java application within same tomcat, format : transactionManagerXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	public PlatformTransactionManager transactionManagerMain() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryMain().getObject());
 
		return transactionManager;
	}
 
	@Bean
	@Primary //Only Main DB is needed to set Primary
	//function name must be unique throughout all java application within same tomcat, format : exceptionTranslationXXXXyyy (XXXX stands for application name, yyy stands for DB name)
	public PersistenceExceptionTranslationPostProcessor exceptionTranslationMain() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}