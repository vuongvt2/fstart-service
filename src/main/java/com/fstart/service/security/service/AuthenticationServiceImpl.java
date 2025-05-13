package com.fstart.service.security.service;

import com.fstart.service.entity.Role;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.repository.RoleRepository;
import com.fstart.service.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * AuthenticationServiceImpl
 *
 * @author VuongVT2
 * @since 2022/04/16
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(final RoleRepository roleRepository,
                                     final UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean checkUserLogin(final String email, final ERole eRole) {
        Role role = roleRepository.findById(eRole)
                .orElse(null);
        return userRepository.findByEmailAndRole(email, role).isPresent();
    }

    @Override
    public User getUserByEmail(final String email) {
        return userRepository.getByEmail(email);
    }

}
