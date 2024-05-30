package com.insure.rfq.login.service;

import java.util.List;
import java.util.Map;

import com.insure.rfq.login.dto.ChangePasswordDto;
import com.insure.rfq.login.dto.ForgotPasswordDto;
import com.insure.rfq.login.dto.UsersNewDto;
import com.insure.rfq.login.dto.UsersNewDtoGet;

public interface UserService {

	UsersNewDto createUser(Long locationId, Long designationId, Long departmentId, UsersNewDto dto);

	List<UsersNewDto> getAllUsersByLocationId(Long locationId);

	int deleteUser(Long id);

	List<UsersNewDtoGet> getAllUsers();

	public String changePassword(ChangePasswordDto resetPassword, long id);

	public Map<String, String> forgotPasswordSendOtp(ForgotPasswordDto forgotPassword);

	public Map<String, String> forforgotPasswordSendPassword(ForgotPasswordDto otp);

}
