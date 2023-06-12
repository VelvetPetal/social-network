package ru.itgroup.intouch.dto;

import lombok.Data;

@Data
public abstract class AbstractUser {

    private String firstName;
    private String lastName;
    private String email;

}
