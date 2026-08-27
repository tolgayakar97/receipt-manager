package com.tolgayakar.receipt_manager.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Role;
import com.tolgayakar.receipt_manager.Model.User;
import com.tolgayakar.receipt_manager.Model.DTO.RegisterRequest;
import com.tolgayakar.receipt_manager.Model.DTO.RegisterResponse;
import com.tolgayakar.receipt_manager.Repository.UserRepository;

@Service
public class RegisterService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public RegisterService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        // TODO: add if email is exist check.

        registerRequest.print();
        //Passeword encoding.
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword); // Encoded password must be used in there.
        user.setRole(Role.USER);

        // Persist user to db
        userRepository.save(user);

        //TODO: Add DtoService
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(user.getId());
        registerResponse.setEmail(user.getEmail());
        return registerResponse;
    }
}
