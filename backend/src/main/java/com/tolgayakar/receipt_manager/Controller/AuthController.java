package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.tolgayakar.receipt_manager.Model.DTO.RegisterRequest;
import com.tolgayakar.receipt_manager.Model.DTO.RegisterResponse;
import com.tolgayakar.receipt_manager.Service.RegisterService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Performs authentication and authorization operations
 */
@RestController
public class AuthController {

    private final RegisterService registerService;

    public AuthController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerHandler(@RequestBody RegisterRequest registerRequest) {
        //TODO: Add email verifier.
        RegisterResponse registerResponse = registerService.registerUser(registerRequest);
        return ResponseEntity.ok(registerResponse);
    }
}
