package com.mailsense.service;

import com.mailsense.dto.RegisterDto;
import com.mailsense.entity.User;

public interface UserService {
    User register(RegisterDto dto);
    User findByEmail(String email);
    User findById(Long id);
    void resetFailedLoginAttempts(String email);
    int  incrementFailedLoginAttempts(String email);
    void lockAccount(String email, int lockMinutes);
}
