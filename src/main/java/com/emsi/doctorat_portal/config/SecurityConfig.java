package com.emsi.doctorat_portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/static/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CRUCIAL pour que l'ajout (POST) marche
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login").permitAll()
                        // On autorise explicitement les deux zones
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/doctorant/**").hasAuthority("DOCTORANT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        // REMPLACÉ : On utilise un handler pour rediriger selon le rôle
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            for (var auth : authorities) {
                                if (auth.getAuthority().equals("ADMINISTRATEUR")) {
                                    response.sendRedirect("/admin/dashboard");
                                    return;
                                } else if (auth.getAuthority().equals("DOCTORANT")) {
                                    response.sendRedirect("/doctorant/index");
                                    return;
                                }
                            }
                            response.sendRedirect("/");
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}