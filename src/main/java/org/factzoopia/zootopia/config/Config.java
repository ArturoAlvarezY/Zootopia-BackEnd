package org.factzoopia.zootopia.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity

public class Config {

    @Value("${api-endpoint}" ) // localhost:8080/api/v1
    String endpoint;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .logout(out -> out
                .logoutUrl(endpoint + "/logout") // localhost:8080/api/v1/logout
                .deleteCookies("JSESSIONID")
                )
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
            .requestMatchers(HttpMethod.POST, endpoint + "/animals").hasRole("ADMIN")
            .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
    
   @Bean
CorsConfigurationSource corsConfigurationSource() {
	CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
	configuration.setAllowedOrigins(Arrays.asList("localhost:5173"));
	configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", configuration);
	return source;
}

@Bean
InMemoryUserDetailsManager inMemoryUserDetailsManager() {

    UserDetails admin = User.builder()
        .username("flashTheRapidash")
        .password("{bcrypt}$2a$12$CpLpmTSK3LqKcVcD/wJaj.URGybXR0fCtLnNynPvwlNQDJT8NGH.O") //password
        .roles("ADMIN")
        .build();

        Collection<UserDetails> userDetails = new ArrayList<>();

        userDetails.add(admin);

    return new InMemoryUserDetailsManager(userDetails);  
}
}
