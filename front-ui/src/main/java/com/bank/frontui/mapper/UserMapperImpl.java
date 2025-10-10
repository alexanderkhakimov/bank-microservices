package com.bank.frontui.mapper;

import com.bank.frontui.dto.RegisterUserRequestDto;
import com.bank.frontui.dto.RegistrationUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterUserRequestDto toRegisterUserRequestDtoWithEncodedPassword(
            RegistrationUserForm form) {
        return RegisterUserRequestDto.builder()
                .login(form.login())
                .email(form.email())
                .password(passwordEncoder.encode(form.password()))
                .name(form.name())
                .birthdate(form.birthdate())
                .build();
    }
}
