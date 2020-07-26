//package com.arun.springdynamodbdockerlocal.securityconfig;
//
//import com.arun.springdynamodbdockerlocal.config.SpringSecurityConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//
///**
// * @author arun on 7/25/20
// */
//@Configuration
//@EnableWebSecurity
//public class BasicConfiguration extends WebSecurityConfigurerAdapter {
//    private final SpringSecurityConfig springSecurityConfig;
//
//    @Autowired
//    public BasicConfiguration(SpringSecurityConfig springSecurityConfig) {
//        this.springSecurityConfig = springSecurityConfig;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        String userName = springSecurityConfig.getName();
//        String password = springSecurityConfig.getPassword();
//        List<String> roles = springSecurityConfig.getRoles();
//        String userRole = roles.get(0);
//        String adminRole = roles.get(1);
//
//
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        auth
//                .inMemoryAuthentication()
//                .withUser(userName)
//                .password(encoder.encode(password))
//                .roles(userRole)
//                .and()
//                .withUser("admin")
//                .password(encoder.encode(password))
//                .roles(userRole, adminRole);
//
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .httpBasic();
//    }
//}
