package com.example.EmployeeApp.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.EmployeeApp.Services.AuthProvider;
import com.example.EmployeeApp.Services.RefreshTokenService;
import com.example.EmployeeApp.Config.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@Controller
public class LoginController {

    private final AuthProvider authProvider;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // Inject the services using constructor injection
    public LoginController(AuthProvider authProvider, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authProvider = authProvider;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Display the login form
    @GetMapping({"/login","/"})
    public String showLoginForm() {
        return "login"; // return the login.html template
    }

    // Process the login form
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute AuthenticationRequest authRequest, Model model, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = authProvider.authenticate(authRequest);
            // Set the JWT as a cookie on the response
            Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
            jwtCookie.setHttpOnly(true);

            response.addCookie(jwtCookie);

            // Set the Refresh Token as another cookie on the response
            Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
            refreshTokenCookie.setMaxAge(60 * 60 * 24 * 6); // This sets the max age to 6 days
            refreshTokenCookie.setHttpOnly(true);
            response.addCookie(refreshTokenCookie);


            return "redirect:/list"; // if login is successful

        } catch (AuthenticationServiceException e) {
            // if login fails, add an error message to the model and return the login page again
            model.addAttribute("error", "Login failed. Please try again.");
            return "redirect:/login";
        }
    }

    @PostMapping("/refresh/token")
    @ResponseBody
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtService.generateTokenWithUserName(refreshTokenRequest.getUserName());
        String newRefreshToken = refreshTokenService.generateRefreshToken().getToken();

        return AuthenticationResponse.builder()
                .token(token)  // changed from authenticationToken
                .refreshToken(newRefreshToken)
                .expiresAt(Instant.now().plusMillis(jwtService.getJwtExpirationInMillis()))
                .userName(refreshTokenRequest.getUserName())
                .build();
    }
}