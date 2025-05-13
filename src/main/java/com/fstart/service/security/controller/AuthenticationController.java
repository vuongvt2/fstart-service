package com.fstart.service.security.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Constant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.utils.GoogleUtils;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EUserStatus;
import com.fstart.service.model.common.GoogleData;
import com.fstart.service.repository.RoleRepository;
import com.fstart.service.repository.UserRepository;
import com.fstart.service.security.model.AuthenticationRequest;
import com.fstart.service.security.model.AuthenticationResponse;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.security.service.AuthenticationService;
import com.fstart.service.security.service.UserDetailsImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AuthenticationController
 *
 * @author VuongVT2
 * @since 2021/10/22
 */
@RestController
@RequestMapping(value = "/fs/api/v1/auth")
public class AuthenticationController {

    private final Constant constant;
    private final Message message;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final AuthenticationService authenticationService;

    private final GoogleUtils googleUtils;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthenticationController(final Constant constant,
                                    final Message message,
                                    final AuthenticationManager authenticationManager,
                                    final AccessTokenService accessTokenService,
                                    final AuthenticationService authenticationService,
                                    final GoogleUtils googleUtils,
                                    final UserRepository userRepository,
                                    final RoleRepository roleRepository) {
        this.constant = constant;
        this.message = message;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.googleUtils = googleUtils;
        this.roleRepository = roleRepository;
    }

    @PostMapping(value = "/ulogin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userLogin(@Valid @RequestBody AuthenticationRequest authRequest) {
        if (!authenticationService.checkUserLogin(authRequest.getUsername(), ERole.USER))
            throw new BadCredentialsException(message.getErrorUnauthorized());

        return login(authRequest);
    }

    @PostMapping(value = "/alogin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> adminLogin(@Valid @RequestBody AuthenticationRequest authRequest) {
        if (!authenticationService.checkUserLogin(authRequest.getUsername(), ERole.ADMIN))
            throw new BadCredentialsException(message.getErrorUnauthorized());

        return login(authRequest);
    }

    private ResponseEntity<?> login(AuthenticationRequest authRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(message.getErrorUnauthorized());
        }

        if (Objects.isNull(authentication)) {
            throw new BadCredentialsException(message.getErrorUnauthorized());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = accessTokenService.generateAccessToken(userDetails);
        String role = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList()).get(0);
        String expirationTime = LocalDateTime.now().plusMinutes(constant.getJwtExpiration()).format(DateTimeFormatter.ofPattern(AppConstant.DTF_dd_MM_yyyy_HH_mm_ss));

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .username(authRequest.getUsername())
                .accessToken(accessToken)
                .role(role)
                .expirationTime(expirationTime)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping(value = "/process-login-google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processLoginGoogle(@RequestParam("accessTokenGoogle") @NotBlank String accessTokenGoogle,
                                                HttpServletRequest request) throws IOException {

        GoogleData googleData = googleUtils.getUserInfo(accessTokenGoogle);
        UserDetails userDetail = googleUtils.buildUser(googleData);
        String role;
        String expirationTime = LocalDateTime.now().plusMinutes(constant.getJwtExpiration()).format(DateTimeFormatter.ofPattern(AppConstant.DTF_dd_MM_yyyy_HH_mm_ss));
        com.fstart.service.entity.User user = authenticationService.getUserByEmail(googleData.getEmail());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        String id;
        if (Objects.nonNull(user)) {
            id = user.getId();
            role = user.getRole().getId().name();
        } else {
            id = IDGenerator.generateID(userRepository, 10);
            user = User.builder()
                    .id(id)
                    .firstName(googleData.getGiven_name())
                    .lastName(googleData.getFamily_name())
                    .email(googleData.getEmail())
                    .createdAt(TimeUtils.comNowDatetime())
                    .updatedAt(TimeUtils.comNowDatetime())
                    .avatar(googleData.getPicture())
                    .role(roleRepository.getById(ERole.USER))
                    .status(EUserStatus.NEW_ACTIVE)
                    .build();
            userRepository.saveAndFlush(user);
            role = ERole.USER.name();
        }
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = accessTokenService.generateAccessTokenFromUser(id, googleData.getEmail(), role);
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .username(googleData.getEmail())
                .accessToken(accessToken)
                .role(role)
                .expirationTime(expirationTime)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping(value = "/login-google", produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView loginGoogle(@RequestParam("code") @NotBlank String code) throws IOException {
        String accessTokenGoogle = googleUtils.getToken(code);
        RedirectView redirectView = new RedirectView();
        String url = new StringBuilder(constant.getAppHost())
                .append(String.format(constant.getAppRedirectURIGoogle(), accessTokenGoogle))
                .toString();
        redirectView.setUrl(url);
        return redirectView;
    }
}
