package com.tolgayakar.receipt_manager.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.DTO.LoginRequest;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Authentication getAuthenticationToken(LoginRequest loginRequest) {
        //We need to return authenticated user
        //To do so, AuthenticationManager should be user which accepts authToken.
        // The token can be obtained from UsernamePasswordAuthenticationToken.
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        //AuthenticationManager uses UserDetailsService
        return authenticationManager.authenticate(authToken);
    }

    public String getJwt(UserDetails userDetails) {
        return jwtService.generateJwt(userDetails);
    }
}