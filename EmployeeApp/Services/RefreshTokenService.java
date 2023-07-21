package com.example.EmployeeApp.Services;

import com.example.EmployeeApp.Config.RefreshToken;
import com.example.EmployeeApp.Config.TokenRefreshException;
import com.example.EmployeeApp.Repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        Instant now = Instant.now();
        refreshToken.setCreatedDate(now);
        refreshToken.setExpiryDate(now.plusSeconds(60 * 60 * 24 * 6));

        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token is not in database!"));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenRefreshException(token, "Refresh token is expired!");
        }
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
