package com.insure.rfq.login.service.impl;

import com.insure.rfq.login.entity.LoginUserDetails;
import com.insure.rfq.login.repository.LoginUserDetailsRepo;
import com.insure.rfq.login.service.LoginUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginUserDetailsServiceImpl implements LoginUserDetailsService {

    @Autowired
    private LoginUserDetailsRepo loginUserDetailsRepo;

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean saveClientDetails(String email) {
        // Retrieve IP address of the client
        String ipAddress = request.getRemoteAddr();

        // Retrieve other details of the client machine
        String userAgent = request.getHeader("User-Agent"); // Client's browser/user-agent
        String hostName = request.getRemoteHost(); // Host name of the client machine

        LoginUserDetails build = LoginUserDetails.builder().ipAddress(ipAddress).userAgent(userAgent).hostName(hostName).createdDate(new Date()).email(email).build();
        LoginUserDetails save = loginUserDetailsRepo.save(build);
        boolean flag = false;
        if (save != null) {
            flag = true;
        }
        return flag;
    }
}
