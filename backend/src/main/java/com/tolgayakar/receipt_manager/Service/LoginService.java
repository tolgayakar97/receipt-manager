package com.tolgayakar.receipt_manager.Service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.DTO.LoginRequest;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;

    public LoginService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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

    public String getJwt(Authentication authenticationToken) {
        return "";
    }
}