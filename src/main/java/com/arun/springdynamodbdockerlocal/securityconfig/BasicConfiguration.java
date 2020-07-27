package com.arun.springdynamodbdockerlocal.securityconfig;

import com.arun.springdynamodbdockerlocal.config.SpringSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author arun on 7/25/20
 */
@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter {
    private final SpringSecurityConfig springSecurityConfig;

    @Autowired
    public BasicConfiguration(SpringSecurityConfig springSecurityConfig) {
        this.springSecurityConfig = springSecurityConfig;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(springSecurityConfig.getName())
                .password("{noop}" + springSecurityConfig.getPassword())
                .roles(springSecurityConfig.getRoles().get(0), springSecurityConfig.getRoles().get(1));

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/v1/**").hasRole(springSecurityConfig.getRoles().get(0))
                .antMatchers("/actuator/**").hasRole(springSecurityConfig.getRoles().get(1))
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }
}
