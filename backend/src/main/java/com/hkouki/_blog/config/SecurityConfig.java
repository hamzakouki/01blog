package com.hkouki._blog.config;

import com.hkouki._blog.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import com.hkouki._blog.security.JwtAuthEntryPoint;
import com.hkouki._blog.security.JwtAccessDeniedHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

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
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
