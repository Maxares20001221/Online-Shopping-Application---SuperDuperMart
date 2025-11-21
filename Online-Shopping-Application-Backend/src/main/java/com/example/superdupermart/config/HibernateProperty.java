package com.example.superdupermart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.jpa.properties.hibernate")
public class HibernateProperty {

    private String dialect;
    private boolean show_sql;
    private boolean format_sql;
    private String hbm2ddl_auto;

    // Getters and Setters
    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public boolean isShow_sql() {
        return show_sql;
    }

    public void setShow_sql(boolean show_sql) {
        this.show_sql = show_sql;
    }

    public boolean isFormat_sql() {
        return format_sql;
    }

    public void setFormat_sql(boolean format_sql) {
        this.format_sql = format_sql;
    }

    public String getHbm2ddl_auto() {
        return hbm2ddl_auto;
    }

    public void setHbm2ddl_auto(String hbm2ddl_auto) {
        this.hbm2ddl_auto = hbm2ddl_auto;
    }
}