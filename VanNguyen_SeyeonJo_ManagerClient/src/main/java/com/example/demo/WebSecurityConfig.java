package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {   
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
		.authorizeHttpRequests((requests) -> requests
//			.requestMatchers("/").permitAll()
//			.requestMatchers("/admin/**").hasAuthority("Admin")
            .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/setupusers")).permitAll() 
            .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("Admin")
            .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
			.anyRequest().authenticated()
		)
		.formLogin((form) -> form
			.loginPage("/login")
			.permitAll()
		)
		.logout((logout) -> logout.permitAll());

	return http.build();

	}
   
    @Bean 
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    }
}
