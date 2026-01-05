package com.hkouki._blog.config;

import com.hkouki._blog.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import com.hkouki._blog.security.JwtAuthEntryPoint;
import com.hkouki._blog.security.JwtAccessDeniedHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final JwtAuthEntryPoint jwtAuthEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

        public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtAuthEntryPoint jwtAuthEntryPoint,
                        JwtAccessDeniedHandler jwtAccessDeniedHandler) {
                this.jwtFilter = jwtFilter;
                this.jwtAuthEntryPoint = jwtAuthEntryPoint;
                this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                        .csrf(csrf -> csrf.disable())
                        .sessionManagement(session -> session
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(ex -> ex
                                        .authenticationEntryPoint(jwtAuthEntryPoint) // 401
                                        .accessDeniedHandler(jwtAccessDeniedHandler) // 403
                        )
                        .authorizeHttpRequests(auth -> auth
                                        .requestMatchers("/api/auth/**").permitAll()
                                        .anyRequest().authenticated())
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
