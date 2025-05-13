package com.fstart.service.security.service;

import com.fstart.service.common.constant.Message;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.EUserStatus;
import com.fstart.service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * UserDetailsServiceImpl
 *
 * @author VuongVT2
 * @since 2021/10/22
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Message message;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(final Message message, final UserRepository userRepository) {
        this.message = message;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatusIn(username, Arrays.asList(new EUserStatus[] {
                EUserStatus.ACTIVE,
                EUserStatus.NEW_ACTIVE
        })).orElseThrow(() -> new UsernameNotFoundException(message.getWarnUserNotFound()));

        return UserDetailsImpl.build(user);
    }

}
