package com.centralway.assessment.phonebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServer extends ResourceServerConfigurerAdapter {

    private static final String SERVER_RESOURCE_ID = "oauth2-server";

    @Bean
    protected TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore()).resourceId(SERVER_RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/user").and().authorizeRequests().antMatchers("/user", "/user/register").permitAll();
        http.requestMatchers().antMatchers("/contacts").and().authorizeRequests().antMatchers("/contacts/**").authenticated();
        http.requestMatchers().antMatchers("/**").and().authorizeRequests().antMatchers("/**").denyAll();
        http.csrf().disable();
    }
}