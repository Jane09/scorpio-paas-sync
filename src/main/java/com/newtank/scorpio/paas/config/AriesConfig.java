package com.newtank.scorpio.paas.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = AriesConfig.PACKAGE, sqlSessionFactoryRef = "ariesSqlSessionFactory")
public class AriesConfig {

    static final String PACKAGE = "com.newtank.scorpio.paas.dao.aries";
    private static final String MAPPER_LOCATION = "classpath:mapper/aries/*.xml";

    private final AriesDbDataNode ariesDbDataNode;

    public AriesConfig(AriesDbDataNode ariesDbDataNode) {
        this.ariesDbDataNode = ariesDbDataNode;
    }

    @Bean(name = "ariesDataSource")
    @Primary
    public DataSource ariesDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(ariesDbDataNode.getDriverClassName());
        dataSource.setUrl(ariesDbDataNode.getUrl());
        dataSource.setUsername(ariesDbDataNode.getUsername());
        dataSource.setPassword(ariesDbDataNode.getPassword());
        return dataSource;
    }

    @Bean(name = "ariesTransactionManager")
    @Primary
    public DataSourceTransactionManager ariesTransactionManager() {
        return new DataSourceTransactionManager(ariesDataSource());
    }

    @Bean(name = "ariesSqlSessionFactory")
    @Primary
    public SqlSessionFactory ariesSqlSessionFactory(@Qualifier("ariesDataSource") DataSource ariesDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(ariesDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(AriesConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
