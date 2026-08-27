package com.tolgayakar.receipt_manager.Model.DTO;

public class LoginRequest {
    private String email;
    private String password;

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmai() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
