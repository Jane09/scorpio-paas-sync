package com.newtank.scorpio.paas.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = HxlConfig.PACKAGE, sqlSessionFactoryRef = "hxlSqlSessionFactory")
public class HxlConfig {
    static final String PACKAGE = "com.newtank.scorpio.paas.dao.hxl";
    private static final String MAPPER_LOCATION = "classpath:mapper/hxl/*.xml";

    private final HxlDbDataNode hxlDbDataNode;

    public HxlConfig(HxlDbDataNode hxlDbDataNode) {
        this.hxlDbDataNode = hxlDbDataNode;
    }

    @Bean(name = "hxlDataSource")
    public DataSource hxlDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(hxlDbDataNode.getDriverClassName());
        dataSource.setUsername(hxlDbDataNode.getUsername());
        dataSource.setUrl(hxlDbDataNode.getUrl());
        dataSource.setPassword(hxlDbDataNode.getPassword());
        return dataSource;
    }

    @Bean(name = "hxlTransactionManager")
    public DataSourceTransactionManager hxlTransactionManager() {
        return new DataSourceTransactionManager(hxlDataSource());
    }

    @Bean(name = "hxlSqlSessionFactory")
    public SqlSessionFactory hxlSqlSessionFactory(@Qualifier("hxlDataSource") DataSource hxlDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(hxlDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(HxlConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
