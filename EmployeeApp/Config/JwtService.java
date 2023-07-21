package com.example.EmployeeApp.Config;

import com.example.EmployeeApp.Controller.AuthenticationResponse;
import com.example.EmployeeApp.Services.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service  // Annotating the class as a service in the context of Spring Framework
public class JwtService
{
    @Autowired
    private RefreshTokenService refreshTokenService;

    // A secret key for signing the JWT (JSON Web Token).
    String secretKey = "rPeBNDfIjkb3Y4F5PM2hp98LypaKYFrHBeWck8k2Q+q61bqYSaLwr+3EP2G4hQoD";
    byte[] secretBytes = secretKey.getBytes();

    // Method to extract the username (subject) from a given JWT
    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    // A generic method to extract specific claims from a given JWT. This method is used internally in other methods.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to generate a JWT for a given user. It's overloaded below to include additional claims.
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // Method to validate a JWT. It checks if the username in the token matches the given user and if the token is not expired.
    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String userName = extractUsername(token);
        return(userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Modify the method to return the JWT as a response body to the frontend
    public ResponseEntity<AuthenticationResponse> login(UserDetails userDetails){
        String token = generateToken(userDetails);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .build();
        return ResponseEntity.ok(response);
    }


    // Private helper method to check if a JWT is expired.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //todo change this back to 20
    public long getJwtExpirationInMillis() {
        return 1000*60*20;  // The value you used for token expiration time in generateToken method
    }

    // An overloaded version of generateToken method. It takes a map of additional claims and a user object and generates a JWT.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails)
    {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+getJwtExpirationInMillis()))  // Token valid for 20 minutes
                .signWith(SignatureAlgorithm.HS256, getSigninKey()) // Using HS256 algorithm for signing
                .compact();  // Building the JWT
    }

    // Private helper method to extract the expiration claim from a JWT.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Private helper method to extract all claims from a JWT.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateTokenWithUserName(String username) {
        // Logic to generate token with the given username...
        UserDetails userDetails = new User(username, "", new ArrayList<>()); // The User object is a basic UserDetails implementation provided by Spring Security
        return generateToken(userDetails);
    }

    // Private helper method to convert the secret key into the format required for signing JWTs.
    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(secretBytes);
    }
}

