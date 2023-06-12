package ru.itgroup.intouch.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthenticateDto {
    @Email(regexp = "Некорректный Email")
    private String email;

    private String password;
}
