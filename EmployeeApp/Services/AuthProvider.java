package com.example.EmployeeApp.Services;

import com.example.EmployeeApp.Config.JwtService;
import com.example.EmployeeApp.Controller.AuthenticationRequest;
import com.example.EmployeeApp.Controller.AuthenticationResponse;
import com.example.EmployeeApp.Controller.RefreshTokenRequest;
import com.example.EmployeeApp.Controller.RegisterRequest;
import com.example.EmployeeApp.Repository.UserRepository;
import com.example.EmployeeApp.dbobjects.Role;
import com.example.EmployeeApp.dbobjects.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProvider //Plays the role of "UserDetailsService"
{
    private final UserRepository uRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse register(RegisterRequest request)
    {
        var user = Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        uRepo.save(user);
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse auth = new AuthenticationResponse();
        auth.setToken(jwtToken);
        var refreshToken = refreshTokenService.generateRefreshToken().getToken();
        auth.setRefreshToken(refreshToken);
        return auth;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = uRepo.findUserByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse auth = new AuthenticationResponse();
        auth.setToken(jwtToken);
        var refreshToken = refreshTokenService.generateRefreshToken().getToken();
        auth.setRefreshToken(refreshToken);
        return auth;
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        var user = uRepo.findUserByEmail(refreshTokenRequest.getUserName()).orElseThrow();

        // Generate a new token for the user
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse auth = new AuthenticationResponse();
        auth.setToken(jwtToken);

        // Create a new refresh token
        var refreshToken = refreshTokenService.generateRefreshToken().getToken();
        auth.setRefreshToken(refreshToken);

        return auth;
    }
}
