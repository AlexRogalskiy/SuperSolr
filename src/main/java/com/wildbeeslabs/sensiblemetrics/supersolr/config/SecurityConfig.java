/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.supersolr.config;

import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAccessDeniedHandler;
import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAuthenticationEntryPoint;
import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAuthenticationSuccessHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Custom web security configuration {@link WebSecurityConfigurerAdapter}
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//@ImportResource({ "classpath:spring-security.config.xml" })
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("user123").roles("USER").build());
        manager.createUser(User.withUsername("admin").password("admin123").roles("ADMIN").build());
        manager.createUser(User.withUsername("dba").password("dba123").roles("ADMIN", "DBA").build());
        manager.createUser(User.withUsername("epadmin").password("epadmin123").roles("EPADMIN").build());
        return manager;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("user123").roles("USER")
                .and()
                .withUser("admin").password("admin123").roles("ADMIN")
                .and()
                .withUser("dba").password("dba123").roles("ADMIN", "DBA")
                .and()
                .withUser("epadmin").password("epadmin123").roles("EPADMIN");
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().realmName("REST API")
                //                .exceptionHandling()
                //                .accessDeniedHandler(accessDeniedHandler)
                //                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().authorizeRequests()
                .antMatchers("/api/**").hasAnyRole("USER", "ADMIN", "DBA")
                .antMatchers("/*").permitAll()
                //.anyRequest().authenticated()
                //.usernameParameter("ssid").passwordParameter("password")
                //.and().exceptionHandling().accessDeniedPage("/denied")
                //                .antMatchers("/api/**").authenticated()
                //                .and().formLogin().loginPage("/api/login")
                //                .successHandler(authenticationSuccessHandler)
                //                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and().headers().cacheControl().disable()
                .and().logout();
    }

    @Bean
    public SecurityAccessDeniedHandler authenticationAccessDeniedHandler() {
        return new SecurityAccessDeniedHandler();
    }

    @Bean
    public SecurityAuthenticationEntryPoint authenticationEntryPoint() {
        return new SecurityAuthenticationEntryPoint();
    }

    @Bean
    public SecurityAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SecurityAuthenticationSuccessHandler();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }
}
