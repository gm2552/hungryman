package com.java.example.tanzu.hungryman.springconfig;

import org.springframework.boot.actuate.autoconfigure.metrics.web.client.HttpClientMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@Configuration
@EnableReactiveFeignClients({"com.java.example.tanzu.hungryman.feign"})
@EnableAutoConfiguration(exclude = HttpClientMetricsAutoConfiguration.class)
public class HungrymanTestConfiguration 
{

}
