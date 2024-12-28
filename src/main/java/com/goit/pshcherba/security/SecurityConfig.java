package com.goit.pshcherba.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * SecurityConfig is a configuration class that sets up Spring Security for the application.
 * It defines beans for password encoding, user details service, and security filter chain.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


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
     * Configures and returns a {@link UserDetailsService} implementation using JDBC.
     * This method creates a {@link JdbcUserDetailsManager} that loads user details (username, password, and enabled status)
     * and authorities (roles) from the database.
     *
     * The {@link JdbcUserDetailsManager} is configured with SQL queries to retrieve the user and role data
     * required by Spring Security during the authentication process.
     *
     * @param dataSource The DataSource that provides the database connection.
     * @return A configured {@link UserDetailsService} instance for Spring Security.
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("select name, password, enabled from goit.userinfo where name=?");
        userDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT goit.userinfo.name username, goit.role.name authority " +
                        "FROM goit.userinfo " +
                        "JOIN goit.user_role ON goit.userinfo.id = goit.user_role.user_id " +
                        "JOIN goit.role ON goit.user_role.role_id = goit.role.id " +
                        "WHERE goit.userinfo.name = ?"
        );
        return userDetailsManager;
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
