package com.fstart.service.security;

import com.fstart.service.security.config.JwtAuthenticationEntryPoint;
import com.fstart.service.security.config.JwtSecurityConfigurer;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * SecurityConfig
 *
 * @author VuongVT2
 * @since 2021/10/03
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccessTokenService accessTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(final AccessTokenService accessTokenService,
                          final UserDetailsServiceImpl userDetailsService,
                          final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.accessTokenService = accessTokenService;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                var cors = new CorsConfiguration();
                cors.setAllowedOrigins(List.of("*"));
                cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                cors.setAllowedHeaders(List.of("*"));
                return cors;
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter: off
        http
            .csrf()
            .disable()
            .cors()
                .configurationSource(corsConfigurationSource())
        .and()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/fs/api/v1/auth/**").permitAll()
            .antMatchers("/fs/api/v1/mock/**").permitAll()
            .antMatchers("/fs/api/v1/master/no-auth").permitAll()
            .antMatchers("/fs/api/v1/master/public/**").permitAll()
            .antMatchers("/fs/api/v1/project/public/**").permitAll()
            .antMatchers("/fs/api/v1/user/public/**").permitAll()
            .antMatchers("/fs/api/v1/tag/public/**").permitAll()
            .antMatchers("/fs/api/v1/news/public/**").permitAll()
            .antMatchers("/fs/api/v1/event/public/**").permitAll()
            .antMatchers("/fs/api/v1/discussion/public/**").permitAll()
            .antMatchers("/fs/api/v1/startup/public/**").permitAll()
            .antMatchers("/fs/api/v1/contact/public/**").permitAll()
            .anyRequest().authenticated()
        .and()
            .httpBasic()
        .and()
            .apply(new JwtSecurityConfigurer(accessTokenService, userDetailsService));
    }
}
