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
import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAuditorAwareHandler;
import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAuthenticationEntryPoint;
import com.wildbeeslabs.sensiblemetrics.supersolr.security.SecurityAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Custom web security configuration {@link WebSecurityConfigurerAdapter}
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
//@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//@ImportResource({ "classpath:spring-security.config.xml" })
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(
            User.withUsername("user")
                .password("user123")
                .roles("USER")
                .authorities("PERM_READ", "PERM_WRITE")
                .build()
        );
        userDetailsManager.createUser(
            User.withUsername("manager")
                .password("manager123")
                .roles("MANAGER")
                .authorities("PERM_READ", "PERM_WRITE", "PERM_MANAGE")
                .build()
        );
        return userDetailsManager;
    }

    @Autowired
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //.anyRequest().authenticated()
        //.usernameParameter("ssid").passwordParameter("password")
        //.and().exceptionHandling().accessDeniedPage("/denied")
        //                .antMatchers("/api/**").authenticated()
        //                .and().formLogin().loginPage("/api/login")
        //                .successHandler(authenticationSuccessHandler)
        //                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
        //.and().logout();

        http.httpBasic().realmName("REST API").and()
            .exceptionHandling().and()
            //.accessDeniedHandler(authenticationAccessDeniedHandler())
            //.authenticationEntryPoint(restAuthenticationEntryPoint)
            .authorizeRequests()
            .antMatchers("/api/search").permitAll()
            .antMatchers("/api/**").hasAnyRole("USER", "MANAGER").and()
//            .antMatchers(HttpMethod.GET, "/api/order").hasRole("USER")
//            .antMatchers(HttpMethod.POST, "/api/order").hasRole("USER")
//            .antMatchers(HttpMethod.PUT, "/api/order").hasRole("USER")
//            .antMatchers(HttpMethod.DELETE, "/api/order").hasRole("MANAGER")
//            .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("MANAGER").and()
            .headers().cacheControl().disable().and()
            .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuditorAware<UserDetails> auditorAware() {
        return new SecurityAuditorAwareHandler();
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
