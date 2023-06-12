package ru.itgroup.intouch.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {
    @Pattern(regexp = ".{8,}",
            message = "Пароль должен состоять не менее чем из 8 символов")
    private String password;
}
