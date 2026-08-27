package com.tolgayakar.receipt_manager.Service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.RmUser;
import com.tolgayakar.receipt_manager.Repository.RmUserRepository;

@Service
public class RmUserDetailsService implements UserDetailsService {
    private final RmUserRepository userRepository;

    public RmUserDetailsService(RmUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        Optional<RmUser> opt = userRepository.findByEmail(username);
        if(opt.isEmpty()) {
            throw new UsernameNotFoundException("User (" + username + ") not found!");
        }

        RmUser rmUser = opt.get();
        return User.withUsername(rmUser.getEmail())
            .password(rmUser.getPassword())
            .roles(rmUser.getRole().name())
            .build();
    }
}
