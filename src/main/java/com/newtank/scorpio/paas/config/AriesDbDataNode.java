package com.newtank.scorpio.paas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aries.datasource")
public class AriesDbDataNode extends DbDataNode {


}
