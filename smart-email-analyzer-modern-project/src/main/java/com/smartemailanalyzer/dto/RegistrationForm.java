package com.smartemailanalyzer.dto;

/**
 * Registration form backing bean.
 */
public class RegistrationForm {

    private String name;
    private String email;
    private String password;
    private String googleAppPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoogleAppPassword() {
        return googleAppPassword;
    }

    public void setGoogleAppPassword(String googleAppPassword) {
        this.googleAppPassword = googleAppPassword;
    }
}
