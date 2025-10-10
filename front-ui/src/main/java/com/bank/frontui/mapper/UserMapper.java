package com.bank.frontui.mapper;

import com.bank.frontui.dto.RegisterUserRequestDto;
import com.bank.frontui.dto.RegistrationUserForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    RegisterUserRequestDto toRegisterUserRequestDtoWithEncodedPassword(RegistrationUserForm form);
}
