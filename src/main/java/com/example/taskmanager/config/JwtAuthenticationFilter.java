/*
* JWT Filter
* Spring Security needs a way to intercept every incoming HTTP request, check for a JWT in the Authorization header, validate it, and if valid → set the authenticated user into the SecurityContext.
* That’s what our JwtAuthenticationFilter will do.
*/

package com.example.taskmanager.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.taskmanager.service.JwtService;
//import com.example.taskmanager.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/*
 * JwtAuthenticationFilter:
 * - Runs ONCE per request (because it extends OncePerRequestFilter)
 * - Checks the Authorization header for a JWT token
 * - Validates the token
 * - If valid → sets the user in Spring SecurityContext (so controllers know the user is authenticated)
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // This class can be implemented to create a JWT authentication filter
    // that intercepts incoming requests and validates the JWT token.

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /*
     * // Constructor injection (Spring will inject JwtService and UserService )
     * public JwtAuthenticationFilter(JwtService jwtService, UserService
     * userService) {
     * this.jwtService = jwtService;
     * this.userService = userService;
     * }
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // if no Authorization header OR it doesn't start with "Bearer" , just continue
        // example: Authorization: Bearer <token>

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // skip this filter
            return;
        }

        // 3. Extract the actual token (skip "Bearer ")
        jwt = authHeader.substring(7); // Bearer<space> is 7 characters

        // 4. Extract username from JWT (we stored it as the subject when generating the
        // token)
        username = jwtService.extractUsername(jwt);

        // 5. Check if :
        // - username is not null
        // - user is not already authenticated in this request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load user details from DB (Spring UserDetails object)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. Validate the token against the user's username
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                // 8. Create an authentication object with the user's details and roles
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // Principal (user info))
                        null, // no credentials since already authenticated
                        userDetails.getAuthorities() // roles
                );

                // 9. Attach request-specific details (IP, session Id, etc)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Set this authentication object in the SecurityContext
                // => Now Spring Security knows this user is authenticated

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continue with the filter chain (next filter or eventuall the controller)
        filterChain.doFilter(request, response);
    }
}