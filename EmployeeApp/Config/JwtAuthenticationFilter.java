package com.example.EmployeeApp.Config;
import com.example.EmployeeApp.Controller.AuthenticationResponse;
import com.example.EmployeeApp.Controller.RefreshTokenRequest;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Gson gson;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Exclude /login and /refresh/token requests from being authenticated
        if ("/login".equals(requestURI) || "/refresh/token".equals(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract JWT and refresh token from cookies
        String jwt = getCookieValue(request, "jwt");
        String refreshToken = getCookieValue(request, "refreshToken");

        if (jwt != null) {
            try {
                authenticateUser(request, jwt);
            } catch (ExpiredJwtException ex) {
                // Handle refresh token
                if (refreshToken != null) {
                    try {
                        AuthenticationResponse newTokens = refreshTokens(refreshToken, jwtService.extractUsername(jwt));
                        jwt = newTokens.getToken();
                        refreshToken = newTokens.getRefreshToken();

                        setCookie(response, "jwt", jwt);
                        setCookie(response, "refreshToken", refreshToken);

                        authenticateUser(request, jwt);
                    } catch (ExpiredJwtException e) {
                        deleteCookie(response, "jwt");
                        deleteCookie(response, "refreshToken");
                        redirectToLogin(response);
                        return;
                    }
                } else {
                    deleteCookie(response, "jwt");
                    redirectToLogin(response);
                    return;
                }
            }
        } else if (refreshToken != null) {
            // Use the refresh token to get a new JWT
            try {
                AuthenticationResponse newTokens = refreshTokens(refreshToken, "");
                jwt = newTokens.getToken();
                refreshToken = newTokens.getRefreshToken();

                setCookie(response, "jwt", jwt);
                setCookie(response, "refreshToken", refreshToken);

                authenticateUser(request, jwt);
            } catch (ExpiredJwtException e) {
                deleteCookie(response, "refreshToken");
                redirectToLogin(response);
                return;
            }
        } else {
            redirectToLogin(response);
            return;
        }

        chain.doFilter(request, response);
    }


    private void authenticateUser(HttpServletRequest request, String jwt) {
        String userEmail = jwtService.extractUsername(jwt);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    private AuthenticationResponse refreshTokens(String refreshToken, String userEmail) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Build the refresh token request body
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken, userEmail);

        // Convert to JSON using the injected Gson instance
        String json = gson.toJson(refreshTokenRequest);

        // Create the request
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://localhost:8080/api/v1/auth/refresh/token")
                .post(requestBody)
                .build();

        // Execute the request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Parse the response body using the injected Gson instance
            if (response.body() != null) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                return gson.fromJson(responseBody, AuthenticationResponse.class);
            } else {
                throw new IOException("Unexpected code "+ response);
            }

        }
    }
}


