package ru.itgroup.intouch.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountDto {

    private int id;
    @NotBlank(message = "Поле \"Имя\" не заполнено")
    private String firstName;
    @NotBlank(message = "Поле \"Фамилия\" не заполнено")
    private String lastName;
    private String email;
    private boolean isDeleted;
    @JsonIgnore
    private String password;
    @Pattern(regexp = "7?[0-9]{10}", message = "Некорректный номер телефона: должен состоять из 11 цифр")
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String statusCode;
    private String regDate;
    @Pattern(regexp = "\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+([+-][0-2]\\d:[0-5]\\d|Z)",
    message = "Некорректная дата рождения")
    private String birthDate;
    private String messagePermission;
    private String lastOnlineTime;
    private boolean isOnline;
    private boolean isBlocked;
    private String photoId;
    private String photoName;
    private String createdOn;
    private String updateOn;
}
