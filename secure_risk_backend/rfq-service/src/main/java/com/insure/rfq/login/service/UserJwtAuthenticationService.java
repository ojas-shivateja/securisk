package com.insure.rfq.login.service;

import java.util.Optional;

import com.insure.rfq.login.dto.AuthenticationDto;
import com.insure.rfq.login.dto.RefreshAuthDto;
import com.insure.rfq.login.dto.UserInfo;
import com.insure.rfq.login.entity.RefreshToken;

public interface UserJwtAuthenticationService {
UserInfo getAuthenticated(AuthenticationDto authentication);
RefreshToken VerifyTokenExpiration(RefreshToken refreshToken);
Optional<RefreshToken>validateRefreshToken(String refreshToken);
UserInfo getAuthenticatedRefreshToken(RefreshAuthDto refreshAuthDto);

}
