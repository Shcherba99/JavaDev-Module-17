package com.goit.pshcherba.security;


import com.goit.pshcherba.entity.User;
import com.goit.pshcherba.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * SecurityConfig is a configuration class that sets up Spring Security for the application.
 * It defines beans for password encoding, user details service, and security filter chain.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;


    /**
     * Defines a bean for the {@link PasswordEncoder} using BCrypt hashing.
     *
     * @return an instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Defines a bean for the {@link UserDetailsService}, which loads user-specific data.
     * It retrieves a {@link User} by username from the database and maps roles to granted authorities.
     *
     * @return a lambda implementation of {@link UserDetailsService}.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository
                    .findByName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getName(),
                    user.getPassword(),
                    user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toList())
            );
        };
    }


    /**
     * Configures the security filter chain for the application.
     * It disables CSRF protection, restricts access to URLs, enables form login, and configures logout.
     *
     * @param http the {@link HttpSecurity} to configure.
     * @return a {@link SecurityFilterChain} with the configured security rules.
     * @throws Exception if an error occurs while configuring the filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auths -> auths
                        .requestMatchers("/**").hasRole("ADMIN")
                )
                .formLogin(withDefaults())
                .logout(withDefaults());

        return http.build();
    }
}
