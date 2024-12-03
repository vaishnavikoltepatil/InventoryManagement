package com.Inventory_management_System.auth.utils;

import com.Inventory_management_System.auth.entities.User;
import com.Inventory_management_System.auth.entities.RefreshToken;
import com.Inventory_management_System.auth.repositories.RefreshTokenRepository;
import com.Inventory_management_System.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String accessToken;

    private String refreshToken;

    @Service
    public static class RefreshTokenService {

        private final UserRepository userRepository;

        private final RefreshTokenRepository refreshTokenRepository;

        public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
            this.userRepository = userRepository;
            this.refreshTokenRepository = refreshTokenRepository;
        }

        public RefreshToken createRefreshToken(String email) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

            RefreshToken refreshToken = user.getRefreshToken();

            if (refreshToken == null) {
                long refreshTokenValidity = 30 * 1000;
                refreshToken = RefreshToken.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                        .user(user)
                        .build();

                refreshTokenRepository.save(refreshToken);
            }

            return refreshToken;
        }

        public RefreshToken verifyRefreshToken(String refreshToken) {
            RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

            if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
                refreshTokenRepository.delete(refToken);
                throw new RuntimeException("Refresh Token expired");
            }

            return refToken;
        }
    }
}
