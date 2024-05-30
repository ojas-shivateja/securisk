package com.insure.rfq.login.service;

import com.insure.rfq.entity.ClientListAppAccess;
import com.insure.rfq.login.dto.*;
import com.insure.rfq.login.entity.*;
import com.insure.rfq.login.repository.DesignationOperationRepository;
import com.insure.rfq.login.repository.OperationRepository;
import com.insure.rfq.login.repository.RefreshTokenRepository;
import com.insure.rfq.login.repository.UserRepositiry;
import com.insure.rfq.repository.ClientListAppAccessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private DesignationOperationRepository designationOperationRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private UserRepositiry userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ClientListAppAccessRepository clientListAppAccessRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Checking UserRegisteration for email: {}", email);
        Optional<UserRegisteration> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.info("User found in UserRegisteration: {}", email);
            return User.builder().username(user.get().getEmail()).password(user.get().getPassword())
                    .authorities("ROLE_USER").build();
        }

        log.info("Checking ClientListAppAccess for email: {}", email);
        Optional<ClientListAppAccess> client = clientListAppAccessRepository.findByEmail(email);
        if (client.isPresent()) {
            log.info("User found in ClientListAppAccess: {}", email);
            return User.builder().username(client.get().getEmail()).password(client.get().getPassword())
                    .authorities("ROLE_USER").build();
        }

        log.warn("User not found with email: {}", email);
        return null; // User not found, return null
    }

    public UserInfo login(AuthenticationDto authenticationDto) {
        String username = authenticationDto.getUsername();
        UserDetails userDetails = loadUserByUsername(username);
        log.info("Attempting login for username: {}", username);

        // Check UserRegisteration table first
        UserRegisteration userOptional = userRepository.findByEmail(username).orElse(null);
        if (userOptional != null) {
            log.info("User found in UserRegisteration: {}", username);
            // Perform password check and other authentication logic for UserRegisteration
            if (passwordMatches(authenticationDto.getPassword(), userOptional.getPassword())) {
                log.info("Password matches for username: {}", username);
                // Perform successful login actions
                String accessToken = jwtService.generateToken(userDetails);
                String refreshToken = generateRefreshToken(username);
                return buildUserInfo(authenticationDto, userOptional, accessToken, refreshToken, false);
            } else {
                log.warn("Invalid password for username: {}", username);
                throw new IllegalArgumentException("Invalid password");
            }
        }

        // If user not found in UserRegisteration, check ClientListAppAccess table
        ClientListAppAccess clientOptional = clientListAppAccessRepository.findByEmail(username).orElse(null);
        if (clientOptional != null) {
            log.info("User found in ClientListAppAccess: {}", username);
            // Perform password check and other authentication logic for ClientListAppAccess
            if (passwordMatches(authenticationDto.getPassword(), clientOptional.getPassword())) {
                log.info("Password matches for username: {}", username);
                // Perform successful login actions
                String accessToken = jwtService.generateToken(userDetails);
                return buildUserInfo(authenticationDto, clientOptional, accessToken, null, true);
            } else {
                log.warn("Invalid password for username: {}", username);
                throw new IllegalArgumentException("Invalid password");
            }
        }

        // If user not found in either table, throw exception
        log.error("User not found with email: {}", username);
        throw new NoSuchElementException("User not found with email: " + username);
    }

    private UserInfo buildUserInfo(AuthenticationDto authenticationDto, Object userOrClient, String accessToken,
                                   String refreshToken, boolean isClient) {
        String userId;
        String department = "";
        String designation = "";
        Long departmentId = null;
        Long locationId = null;
        String location = null;
        Long designationId = null;
        Long clientListId = null;
        Long productId = null;
        String employeeId = null;
        String firstName = null;
        String lastName = null;
        Set<OperationMapped> listOfOperationMapped = null;

        if (isClient) {
            ClientListAppAccess client = (ClientListAppAccess) userOrClient;
            userId = String.valueOf(client.getAppAccessId());
            clientListId = client.getClientList().getCid();
            productId = client.getProduct().getProductId();
            employeeId = client.getEmployeeId();
            Department departmentObj = client.getDepartment();
            firstName = client.getEmployeeName();
            if (departmentObj != null) {
                department = departmentObj.getDepartmentName();
                departmentId = departmentObj.getId();
            }
            Designation designationObj = client.getDesignation();
            if (designationObj != null) {
                designation = designationObj.getDesignationName();
                designationId = designationObj.getId();
            }
        } else {
            UserRegisteration user = (UserRegisteration) userOrClient;
            userId = String.valueOf(user.getUserId());
            department = user.getDepartment().getDepartmentName();
            departmentId = user.getDepartment().getId();
            designationId = user.getDesignation().getId();
            designation = user.getDesignation().getDesignationName();
            locationId = user.getLocation().getLocationId();
            location = user.getLocation().getLocationName();
            firstName = user.getFirstName();
            lastName = user.getLastName();

            List<Long> listOfOperationId = designationOperationRepository
                    .findByOperationId(user.getDesignation().getId());
            listOfOperationMapped = new LinkedHashSet<>();
            Set<String> getAllPermissionUpdate = new LinkedHashSet<>();

            for (Long value : listOfOperationId) {
                Optional<OperationTable> operationObj = operationRepository.findById(value);
                if (operationObj.isPresent()) {
                    List<Long> operationTable = operationRepository
                            .getAllMenuNameBasedOnMenuTypeObj(operationObj.get().getMenuType());
                    List<Long> allPermittedOperationByDesignationId = designationOperationRepository
                            .getAllPermittedOperationByDesignationId(user.getDesignation().getId());
                    List<SubOperation> subOperation = new LinkedList<>();

                    for (Long id : operationTable) {
                        SubOperation subOperationTable = new SubOperation();
                        subOperationTable.setOperationName(operationRepository.findById(id).get().getMenuName());
                        subOperationTable.setOperationFlag(allPermittedOperationByDesignationId.contains(id));
                        subOperation.add(subOperationTable);
                    }

                    OperationMapped operation = OperationMapped.builder()
                            .operationType(operationObj.get().getMenuType()).isOperationMapped(true)
                            .subOperation(subOperation).build();
                    listOfOperationMapped.add(operation);
                    getAllPermissionUpdate.add(operationObj.get().getMenuType());
                }
            }

            for (String data : operationRepository.allOPerationMenuType()) {
                if (getAllPermissionUpdate.add(data)) {
                    OperationMapped operation = OperationMapped.builder().operationType(data).isOperationMapped(false)
                            .subOperation(null).build();
                    listOfOperationMapped.add(operation);
                }
            }
        }

        UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.builder().accessToken(accessToken)
                .refreshToken(refreshToken).userId(Long.parseLong(userId))
                .locationInfo(LocationInfo.builder().locationId(locationId).locationName(location)
                        .isLocationInfoPermitted(true).build())
                .departmentInfo(DepartmentInfo.builder().departmentId(departmentId).isDepartmentInfoPermitted(true)
                        .departmentName(department).build())
                .designationBasedOperation(DesignationBasedOperation.builder().operationMapped(listOfOperationMapped)
                        .desginationId(designationId).isDesignationBasedOperationPermitted(true)
                        .designationName(designation).build())
                .firstName(firstName)
                .lastName(lastName)
                .clientListId(clientListId).productId(productId).employeeId(employeeId).isLogin(true);

        if (isClient) {
            userInfoBuilder.clientId(userId).isClientLogin(true).isBrokerLogin(false);
        } else {
            userInfoBuilder.brokerId(userId).isClientLogin(false).isBrokerLogin(true);
        }

        return userInfoBuilder.build();
    }

    private String generateRefreshToken(String email) {
        String token = UUID.randomUUID().toString();
        Optional<UserRegisteration> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            RefreshToken refreshToken = RefreshToken.builder().token(token).usersNew(userOptional.get())
                    .expiration(Instant.now().plusMillis(600000 * 20)) // Set expiration time
                    .build();
            refreshTokenRepository.save(refreshToken);
            return token;
        } else {
            throw new NoSuchElementException("User not found with email: " + email);
        }

    }

    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
