package com.mailsense.dto;

import lombok.*;
import javax.validation.constraints.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Gmail address is required")
    @Email(message = "Invalid Gmail address")
    private String gmailAddress;

    @NotBlank(message = "Gmail App Password is required")
    private String gmailAppPassword;

    private String imapHost = "imap.gmail.com";
    private int    imapPort = 993;
    private String smtpHost = "smtp.gmail.com";
    private int    smtpPort = 587;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
