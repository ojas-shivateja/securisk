package com.insure.rfq.login.service.impl;

import com.insure.rfq.login.dto.ChangePasswordDto;
import com.insure.rfq.login.dto.ForgotPasswordDto;
import com.insure.rfq.login.dto.UsersNewDto;
import com.insure.rfq.login.dto.UsersNewDtoGet;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.entity.UserRegisteration;
import com.insure.rfq.login.globalexception.InvalidUser;
import com.insure.rfq.login.globalexception.PasswordMismatchException;
import com.insure.rfq.login.repository.DepartmentRepository;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.login.repository.UserRepositiry;
import com.insure.rfq.login.service.UserService;
import com.insure.rfq.utils.EmailSender;
import com.insure.rfq.utils.PasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositiry userRepositiry;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailSender emailSernder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public UsersNewDto createUser(Long locationId, Long designationId, Long departmentId, UsersNewDto dto) {
        log.info("Creating a new user.");
        if (dto != null) {
            Optional<Location> optionalLocation = locationRepository.findById(locationId);
            Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
            Optional<Designation> optionalDesignation = designationRepository.findById(designationId);

            if (optionalLocation.isPresent()) {
                Location location = optionalLocation.get();

                Department department = optionalDepartment.get();

                Designation designation = optionalDesignation.get();

                UserRegisteration usersNew = modelMapper.map(dto, UserRegisteration.class);
                usersNew.setLocation(location);
                usersNew.setDepartment(department);
                usersNew.setDesignation(designation);

                usersNew.setStatus("ACTIVE");

                usersNew.setPassword(encoder.encode(dto.getPassword()));
                usersNew.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
                UserRegisteration savedUser = userRepositiry.save(usersNew);
                UsersNewDto map = modelMapper.map(savedUser, UsersNewDto.class);

//				locationRepository.increaseUserCount(locationId, location.getUserCount() + 1);
                log.info("Created user with ID: {}", map.getUserId());

                return map;
            }
        }
        log.warn("User creation failed.");
        return null;
    }

    @Override
    public List<UsersNewDto> getAllUsersByLocationId(Long locationId) {
        return userRepositiry.getAllUsersByLocationId(locationId).stream()
                .map(users -> modelMapper.map(users, UsersNewDto.class)).toList();
    }

    @Override
    public int deleteUser(Long id) {
        UserRegisteration user = userRepositiry.findById(id).orElseThrow(() -> new InvalidUser(" invalid user"));
        Location location = locationRepository.findById(user.getLocation().getLocationId()).get();
        int number = userRepositiry.deleteUser(user.getUserId());
//		if (number != 0) {
//			locationRepository.increaseUserCount(location.getLocationId(), location.getUserCount() - 1);
//		}
        return number;
    }

    @Override
    public List<UsersNewDtoGet> getAllUsers() {

        return userRepositiry.findAll().stream().map(obj -> {
            log.info("Fetching all active users.");

            UsersNewDtoGet usersNewDtoGet = new UsersNewDtoGet();
            usersNewDtoGet.setUserId(obj.getUserId());
            usersNewDtoGet.setBusinessType(obj.getBusinessType());
            usersNewDtoGet.setCorporateName(obj.getCorporateName());
            usersNewDtoGet.setAge(obj.getAge());
            usersNewDtoGet.setDateOfBirth(obj.getDateOfBirth());
            usersNewDtoGet.setEmployeeId(obj.getEmployeeId());

            usersNewDtoGet.setDepartment(userRepositiry.findDepartmentByUserId(obj.getUserId()).getDepartmentName());
            usersNewDtoGet.setDesignation(userRepositiry.findDesignationByUserId(obj.getUserId()).getDesignationName());
            usersNewDtoGet.setLocation(userRepositiry.findLocationByUserId(obj.getUserId()).getLocationName());

            usersNewDtoGet.setEmail(obj.getEmail());
            usersNewDtoGet.setFirstName(obj.getFirstName());
            usersNewDtoGet.setLastName(obj.getLastName());
            usersNewDtoGet.setGender(obj.getGender());
            usersNewDtoGet.setPhoneNo(obj.getPhoneNo());
            usersNewDtoGet.setStatus(obj.getStatus());
            return usersNewDtoGet;

        }).filter(obj -> obj != null && obj.getStatus().equalsIgnoreCase("ACTIVE")).toList();
    }

    @Override
    public String changePassword(ChangePasswordDto resetPassword, long id) {
        UserRegisteration userData = userRepositiry.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        boolean matches = bCryptPasswordEncoder.matches(resetPassword.getPassword(), userData.getPassword());
        if (matches) {
            if (resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {
                String encodePassword = bCryptPasswordEncoder.encode(resetPassword.getNewPassword());
                userData.setPassword(encodePassword);
                UserRegisteration savePassword = userRepositiry.save(userData);
                if (savePassword != null) {
                    return "password changed successful";
                }
            } else {
                throw new PasswordMismatchException("password mismatch");
            }
        } else {
            throw new PasswordMismatchException("enter valid password");
        }
        return "failed to change password";

    }

    @Override
    public Map<String, String> forgotPasswordSendOtp(ForgotPasswordDto forgotPassword) {
        Optional<UserRegisteration> userOptional = userRepositiry.findByEmail(forgotPassword.getEmail());
        if (userOptional.isPresent()) {
            UserRegisteration userInfo = userOptional.get();
            Map<String, String> map = new HashMap<>();
            String otp = emailSernder.sendEmailforOtp(userInfo.getEmail());
            userInfo.setOtp(otp);
            UserRegisteration save = userRepositiry.save(userInfo);
            if (save != null) {
                map.put("email", save.getEmail());
                map.put("message", "Email sent successfully");
            } else {
                map.put("message", "Failed to send email");
            }
            return map;
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("message", "User does not exist");
            return map;
        }

    }

    public Map<String, String> forforgotPasswordSendPassword(ForgotPasswordDto otp) {
        UserRegisteration userInfo = userRepositiry.findByEmailAndOtp(otp.getEmail(), otp.getOtp())
                .orElseThrow(() -> new InvalidUser("enter valid otp"));
        Map<String, String> map = new HashMap<>();
        String password = passwordGenerator.getRandomPassword();
        log.error("password:{}", password);
        String sendPassword = emailSernder.sendRandomPassword(userInfo.getEmail(), password);
        log.error("password:{}", sendPassword);
        if (password.equals(sendPassword)) {
            String encodePassword = bCryptPasswordEncoder.encode(password);
            log.error("password:{}", encodePassword);
            userInfo.setPassword(encodePassword);
            userInfo.setOtp("");
            UserRegisteration saveUserInfo = userRepositiry.save(userInfo);
            if (saveUserInfo != null) {
                map.put("message", "password sent to respected email address");
            }
        } else {
            map.put("message", "failed to send otp");
        }
        return map;
    }

}
