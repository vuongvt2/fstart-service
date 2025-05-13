package com.fstart.service.security.service;

import com.fstart.service.entity.User;
import com.fstart.service.enumeration.ERole;

/**
 * AuthenticationService
 *
 * @author VuongVT2
 * @since 2022/04/16
 */
public interface AuthenticationService {

    boolean checkUserLogin(String email, ERole eRole);


    User getUserByEmail(String email);

}
