package com.example.EmployeeApp.Controller;

public class RefreshTokenRequest {
    private String refreshToken;
    private String userName;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RefreshTokenRequest() {
    }

    public RefreshTokenRequest(String refreshToken, String userName) {
        this.refreshToken = refreshToken;
        this.userName = userName;
    }
}
