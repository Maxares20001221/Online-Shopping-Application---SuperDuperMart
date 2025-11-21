package com.example.superdupermart.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private HibernateProperty hibernateProperty;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.example.superdupermart.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
    
    /**
     * 显式提供 SessionFactory Bean，标记为 @Primary 确保 DAO 能正确注入
     */
    @Bean
    @Primary
    @DependsOn("sessionFactory")
    public SessionFactory hibernateSessionFactory(LocalSessionFactoryBean sessionFactoryBean) throws Exception {
        // 确保 LocalSessionFactoryBean 已初始化
        if (sessionFactoryBean.getObject() == null) {
            sessionFactoryBean.afterPropertiesSet();
        }
        return sessionFactoryBean.getObject();
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        // 使用 HibernateProperty 的值，如果为 null 则使用默认值
        props.put("hibernate.dialect", 
            hibernateProperty != null && hibernateProperty.getDialect() != null 
                ? hibernateProperty.getDialect() 
                : "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.show_sql", 
            hibernateProperty != null ? hibernateProperty.isShow_sql() : true);
        props.put("hibernate.format_sql", 
            hibernateProperty != null ? hibernateProperty.isFormat_sql() : true);
        props.put("hibernate.hbm2ddl.auto", 
            hibernateProperty != null && hibernateProperty.getHbm2ddl_auto() != null 
                ? hibernateProperty.getHbm2ddl_auto() 
                : "update");
        // 配置当前会话上下文，确保事务同步
        props.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
        return props;
    }

    @Bean(name = "transactionManager")
    @Primary
    @DependsOn("hibernateSessionFactory")
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }
}