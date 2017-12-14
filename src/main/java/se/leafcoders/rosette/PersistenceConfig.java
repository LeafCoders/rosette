package se.leafcoders.rosette;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// @PropertySource("classpath:database.properties") // Eller från application.yaml
public class PersistenceConfig {
/*
	@Autowired
	private Environment env

	@Autowired
	private DataSource dataSource

	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory

	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean()
		lcemfb.setJpaVendorAdapter(getJpaVendorAdapter())
		lcemfb.setDataSource(dataSource)
		lcemfb.setPersistenceUnitName("myJpaPersistenceUnit")
		lcemfb.setPackagesToScan("se.leafcoders.rosette.persistence")
		lcemfb.setJpaProperties(jpaProperties())
		return lcemfb
	}

	@Bean
	JpaVendorAdapter getJpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter()
		return adapter
	}

	@Bean
	DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("database.driverClassName"))
		dataSource.setUrl(env.getProperty("database.url"))
		dataSource.setUsername(env.getProperty("database.username"))
		dataSource.setPassword(env.getProperty("database.password"))
		return dataSource
	}

	@Bean
	PlatformTransactionManager transactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager()
		jpaTransactionManager.entityManagerFactory = entityManagerFactory
		return jpaTransactionManager
	}
*/
	/**
	 * PersistenceExceptionTranslationPostProcessor is a bean post processor
	 * which adds an advisor to any bean annotated with Repository so that any
	 * platform-specific exceptions are caught and then rethrown as one
	 * Spring's unchecked data access exceptions (i.e. a subclass of
	 * DataAccessException).
	 */
/*
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	private Properties jpaProperties() {
		Properties properties = new Properties()
		properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"))
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"))
		properties.put("hibernate.id.new_generator_mappings", env.getProperty("hibernate.id.new_generator_mappings"))
		properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"))
		properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"))
		return properties
	}
*/
}
