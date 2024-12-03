package com.Inventory_management_System.controller;

import com.Inventory_management_System.auth.entities.RefreshToken;
import com.Inventory_management_System.auth.entities.User;
import com.Inventory_management_System.auth.services.RefreshTokenService;
import com.Inventory_management_System.auth.services.UserService;
import com.Inventory_management_System.auth.services.JwtService;
import com.Inventory_management_System.auth.utils.UserResponse;
import com.Inventory_management_System.auth.utils.LoginRequest;
import com.Inventory_management_System.auth.utils.RefreshTokenRequest;
import com.Inventory_management_System.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/")
public class UserController {

    private final UserService UserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public  UserController(UserService UserService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.UserService = UserService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(UserService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(UserService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(UserResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
