package com.continuum.multitenant.tenant.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.continuum.multitenant.mastertenant.config.PhysicalNamingStrategyImpl;

import jdk.internal.org.jline.utils.Log;

/**
 * @author RK
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.continuum.tenant.repos.repositories", "com.continuum.tenant.repos.entity"})
@EnableJpaRepositories(basePackages = {"com.continuum.tenant.repos.repositories", "com.continuum.tenant.service","com.continuum.serviceImpl"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
public class TenantDatabaseConfig {

    @Bean(name = "tenantJpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory tenantEntityManager) {
    	
    	 System.out.println("Step 6====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	 Log.info("Step 6====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(tenantEntityManager);
        return transactionManager;
    }

    /**
     * The multi tenant connection provider
     *
     * @return
     */
    @Bean(name = "datasourceBasedMultitenantConnectionProvider")
    @ConditionalOnBean(name = "masterEntityManagerFactory")
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        // Autowires the multi connection provider
        return new DataSourceBasedMultiTenantConnectionProviderImpl();
    }

    /**
     * The current tenant identifier resolver
     *
     * @return
     */
    @Bean(name = "currentTenantIdentifierResolver")
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    /**
     * Creates the entity manager factory bean which is required to access the
     * JPA functionalities provided by the JPA persistence provider, i.e.
     * Hibernate in this case.
     *
     * @param connectionProvider
     * @param tenantResolver
     * @return
     */
    @Bean(name = "entityManagerFactory")
    @ConditionalOnBean(name = "datasourceBasedMultitenantConnectionProvider")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("datasourceBasedMultitenantConnectionProvider")
                    MultiTenantConnectionProvider connectionProvider,
            @Qualifier("currentTenantIdentifierResolver")
                    CurrentTenantIdentifierResolver tenantResolver) {
    	
    	System.out.println("Step 1====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	 Log.info("Step 1====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        //All tenant related entities, repositories and service classes must be scanned
        emfBean.setPackagesToScan(new String[] { "com.continuum.tenant.repos.repositories", "com.continuum.tenant.repos.entity"
                });
        
        System.out.println("Step 2====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.info("Step 2====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        emfBean.setJpaVendorAdapter(jpaVendorAdapter());
        emfBean.setPersistenceUnitName("tenantdb-persistence-unit");
        Map<String, Object> properties = new HashMap<>();
        
        
        System.out.println("Step 3====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.info("Step 3====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(Environment.SHOW_SQL, true);
        properties.put(Environment.FORMAT_SQL, true);
        
        System.out.println("Step 4====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.info("Step 4====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
       // properties.put(Environment.IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class.getName());
       // properties.put(Environment.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategyStandardImpl.class.getName());
        properties.put(Environment.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategyImpl.class.getName());
        properties.put(Environment.HBM2DDL_AUTO, "none");
        emfBean.setJpaPropertyMap(properties);
        System.out.println("Step 5====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.info("Step 5====================!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        return emfBean;
    }
}
