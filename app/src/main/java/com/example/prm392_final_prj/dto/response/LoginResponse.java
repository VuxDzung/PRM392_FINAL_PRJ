package com.example.prm392_final_prj.dto.response;

public class LoginResponse {

    private String jwtToken;

    public LoginResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LoginResponse() {
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
