/**
 * SecurityConfig
 *
 * - Configures how Spring Security protects endpoints.
 * - Registers the JwtAuthenticationFilter so every incoming request is checked for a valid JWT.
 * - Declares PasswordEncoder and AuthenticationManager beans (used by AuthService, etc.).
 * 
 */

package com.example.taskmanager.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//import com.example.taskmanager.config.JwtAuthenticationFilter; //filter that validates JWT on each request
//import com.example.taskmanager.service.UserService; //implements UserDetailsService in our app

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT filter that validates JWT tokens and populates SecurityContext
    private final JwtAuthenticationFilter jwtAuthFilter;

    // our UserService implements UserDetailsService (user by the authentication
    // flow)
    // private final UserService userService;

    // 1) Password Encoder bean (used by AuthService to hash passwords)
    // Used to hash passwords when registering and to verify passwords during
    // authentication

    /*
     * @Bean
     * public PasswordEncoder passwordEncoder() {
     * return new BCryptPasswordEncoder();
     * } REMOVED here, and made a sepearate since it was creating a circular bean
     * dependency between
     * SecurityConfig, JWTAuthenticationFilter and UserService.
     */
    // 2) Authentication Manager bean (used by AuthService to authenticate username
    // + password)
    // We get the pre-configured AuthenticationManager from the Spring's
    // AuthenticationConfiguration
    // That manager will user UserDetailsService + PasswordEncoder to authenticate
    // users;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // 3) AuthenticationEntryPoint - customize 401 responses
    // When a request fails authentication, Spring will call this entry point
    // We return a compact JSON body instead of the HTML or default text

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String body = String.format(
                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                    Instant.now().toString(),
                    authException.getMessage() == null ? "Unauthorized"
                            : authException.getMessage()
                                    .replace("\"", "'"),
                    request.getRequestURI());
            try {
                response.getWriter().write(body);
            } catch (IOException e) {
                // IF writing fails, there's not much we can do
                e.printStackTrace();
            }
        };
    }

    // 4 ) Main security filter chain

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 4.A Disable CSRF for stateless API (we're using JWTs)
        http.csrf(csrf -> csrf.disable());

        // 4.B Exception handling: use our JSON authenticationEntryPoint for failed auth
        http.exceptionHandling(handlers -> handlers.authenticationEntryPoint(authenticationEntryPoint()));

        // 4.C Session management: make the app stateless (no HTTP session)
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4.D Authorize Requests:
        // - permit access to auth endpoints (login/register) and docs/h2-console
        // - require authentication for everything else

        http.authorizeHttpRequests(auth -> auth.requestMatchers(
                "/api/v1/auth/**", // login/register endpoints
                "/v3/api-docs/**", // OpenAPI docs
                "/swagger-ui/**", // Swagger API
                "/swagger-ui.html",
                "/h2-console/**" // H2 Console (dev only)
        ).permitAll()
                .anyRequest().authenticated());

        // 4.E Insert our JWT filter BEFORE Spring's
        // UsernamePasswordAuthenticationFilter so tokens
        // are processed before any attempts to use form-login or default behavior

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 4.F If you use H2 console in dev , frames must be allowed.
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // build and return the configured chain
        return http.build();
    }
}
