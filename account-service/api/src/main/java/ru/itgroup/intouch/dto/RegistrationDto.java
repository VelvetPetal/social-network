package ru.itgroup.intouch.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationDto extends AbstractUser {
    @Pattern(regexp = ".{8,}",
            message = "Пароль должен состоять не менее чем из 8 символов.")
    private String password1;
    private String password2;
    private String captchaCode;
    private String captchaSecret;
}
