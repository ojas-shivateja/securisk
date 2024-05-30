package com.insure.rfq.login.controller;

import com.insure.rfq.login.dto.AuthenticationDto;
import com.insure.rfq.login.dto.RefreshAuthDto;
import com.insure.rfq.login.dto.UserInfo;
import com.insure.rfq.login.service.AuthenticationService;
import com.insure.rfq.login.service.JwtService;
import com.insure.rfq.login.service.UserJwtAuthenticationService;
import com.insure.rfq.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
@Slf4j
public class UserLoginController {


    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserJwtAuthenticationService userJwtAuthenticationService;
    @Autowired
    private UserJwtAuthenticationService authenticationService;


    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService service;


    @PostMapping("/refreshToken")
    @ResponseStatus(value = HttpStatus.OK)
    public UserInfo getRefreshedToken(@RequestBody RefreshAuthDto authDto) {
        log.info(" process of retrive refresh token started ");
        return userJwtAuthenticationService.getAuthenticatedRefreshToken(authDto);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(value = HttpStatus.OK)
    public UserInfo getLogin(@RequestBody AuthenticationDto authenticationDto) {
        return service.login(authenticationDto);
    }
}
