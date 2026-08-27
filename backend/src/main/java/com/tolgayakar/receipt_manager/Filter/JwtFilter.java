package com.tolgayakar.receipt_manager.Filter;

import com.tolgayakar.receipt_manager.Service.JwtService;
import com.tolgayakar.receipt_manager.Service.RmUserDetailsService;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    //OncePerRequestFilter performs filter for each http request.

    //doFilterInternal method must be implemented.
    private final JwtService jwtService;
    private final RmUserDetailsService rmUserDetailsService;

    JwtFilter(JwtService jwtService, RmUserDetailsService rmUserDetailsService) {
        this.jwtService = jwtService;
        this.rmUserDetailsService = rmUserDetailsService;
    }

    protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
                // In order to authorize, jwt token should be obtained from request.
                // Jwt token can be obtained from request header in format: Authorization: Bearer <jwt>
                String auth = request.getHeader("Authorization");
                System.out.println(auth);
                
                //auth cannot be empty and should start with Bearer 
                if(auth == null || !auth.startsWith("Bearer ")) {
                    System.out.println("No jwt found in request header");
                    filterChain.doFilter(request, response); // Send next step without doing nothing
                    return;
                }

                // Get jwt from auth if auth is valid.
                String jwt = auth.substring(7);

                // username should be extracted from jwt to find who is requesting.
                String username = jwtService.extractUsername(jwt);
                 // Username needed to be found from database with extracted username information
                UserDetails userDetails =  rmUserDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    //Generates Authentication with provided user and role
                    UsernamePasswordAuthenticationToken token = 
                        new UsernamePasswordAuthenticationToken(userDetails,
                                                                null,
                                                                userDetails.getAuthorities());

                // SecurityContextHolder keeps the information of "this request belongs to the user".
                // If jwt is valid, this information is added to SecurityContext
                SecurityContextHolder.getContext().setAuthentication(token);
                }

                filterChain.doFilter(request, response); // Complete filter.
            }
}
