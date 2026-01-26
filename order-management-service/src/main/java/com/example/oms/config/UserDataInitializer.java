package com.example.oms.config;

import com.example.oms.domain.entity.RoleEntity;
import com.example.oms.domain.entity.UserEntity;
import com.example.oms.repository.RoleRepository;
import com.example.oms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class UserDataInitializer {

    public UserDataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        if (userRepository.count() == 0) {

            UserEntity user = new UserEntity();
            user.setId(UUID.randomUUID());
            user.setUsername("customer1");
            user.setPasswordHash(passwordEncoder.encode("password"));
            user.setEnabled(true);

            RoleEntity customerRole = new RoleEntity();
            customerRole.setName("ROLE_CUSTOMER");

            roleRepository.save(customerRole);

            user.setRoles(Set.of(customerRole));

            userRepository.save(user);
        }
    }
}

