package com.newtank.scorpio.paas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hxl.datasource")
public class HxlDbDataNode extends DbDataNode {

    
}
