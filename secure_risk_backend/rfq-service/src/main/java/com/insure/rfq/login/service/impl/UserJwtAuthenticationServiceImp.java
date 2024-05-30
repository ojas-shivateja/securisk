package com.insure.rfq.login.service.impl;

import com.insure.rfq.login.dto.AuthenticationDto;
import com.insure.rfq.login.dto.RefreshAuthDto;
import com.insure.rfq.login.dto.UserInfo;
import com.insure.rfq.login.entity.RefreshToken;
import com.insure.rfq.login.repository.*;
import com.insure.rfq.login.service.JwtService;
import com.insure.rfq.login.service.LoginUserDetailsService;
import com.insure.rfq.login.service.UserJwtAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class UserJwtAuthenticationServiceImp implements UserJwtAuthenticationService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private UserRepositiry newRepositiry;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DesignationOperationRepository designationOperationRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private DesignationRepository designationRepository;
    @Autowired
    private UserRepositiry userRepositiry;
    @Autowired
    private LoginUserDetailsService loginUserDetailsService;
    public static final String INVALID = " invalid user";

    @Override
    public UserInfo getAuthenticated(AuthenticationDto authentication) {
        return null;

    }

    @Override
    public Optional<RefreshToken> validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Override
    public RefreshToken VerifyTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiration().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new UsernameNotFoundException(INVALID);

        } else {
            return refreshToken;
        }
    }

    @Override
    public UserInfo getAuthenticatedRefreshToken(RefreshAuthDto refreshAuthDto) {

        return null;
    }

}
