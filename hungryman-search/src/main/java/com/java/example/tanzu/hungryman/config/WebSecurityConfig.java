package com.java.example.tanzu.hungryman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig 
{
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    http.httpBasic().disable();
        http.authorizeExchange().anyExchange().permitAll();
        http.csrf().disable();
        return http.build();
	}
}
