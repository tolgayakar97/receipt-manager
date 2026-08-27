package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.tolgayakar.receipt_manager.Model.DTO.LoginRequest;
import com.tolgayakar.receipt_manager.Model.DTO.RegisterRequest;
import com.tolgayakar.receipt_manager.Model.DTO.RegisterResponse;
import com.tolgayakar.receipt_manager.Service.LoginService;
import com.tolgayakar.receipt_manager.Service.RegisterService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Performs authentication and authorization operations
 */
@RestController
public class AuthController {

    private final RegisterService registerService;
    private final LoginService loginService;

    public AuthController(RegisterService registerService, LoginService loginService) {
        this.registerService = registerService;
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerHandler(@RequestBody RegisterRequest registerRequest) {
        //TODO: Add email verifier.
        RegisterResponse registerResponse = registerService.registerUser(registerRequest);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        //TODO: Add email verifier.
        Authentication authentication = loginService.getAuthenticationToken(loginRequest);
        return ResponseEntity.ok("Login Test");
    }
    
}
