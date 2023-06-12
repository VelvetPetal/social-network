package ru.itgroup.intouch.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itgroup.intouch.config.email.EmailContentsConfig;
import ru.itgroup.intouch.dto.CaptchaDto;
import ru.itgroup.intouch.dto.EmailDto;
import ru.itgroup.intouch.dto.PasswordDto;
import ru.itgroup.intouch.dto.RegistrationDto;
import ru.itgroup.intouch.exceptions.CaptchaNotValidException;
import ru.itgroup.intouch.exceptions.UserAlreadyRegisteredException;
import ru.itgroup.intouch.service.account.CredentialsRenewalService;
import ru.itgroup.intouch.service.account.RegistrationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final EmailContentsConfig emailContents;
    private final RegistrationService registrationService;
    private final CredentialsRenewalService credentialsRenewalService;


    @PostMapping("/register")
    public void register(@Valid @RequestBody RegistrationDto registrationDto)
            throws UserAlreadyRegisteredException, CaptchaNotValidException {
        registrationService.registerNewUser(registrationDto);
    }

    @PostMapping("/password/recovery/")
    public void recoverPassword(@Valid @RequestBody EmailDto emailDto) throws MessagingException {
        credentialsRenewalService.sendLetter(emailDto.getEmail(),
                emailContents.getPasswordRecovery(), true);

    }

    @PostMapping("/password/recovery/{linkId}")
    public void setNewPassword(@PathVariable String linkId,
                               @Valid @RequestBody PasswordDto passwordDto) {
        credentialsRenewalService.setNewPassword(linkId, passwordDto.getPassword());
    }



    @GetMapping("/captcha")
    public CaptchaDto captcha() {
        return  registrationService.generateCaptcha();
    }



    // FIXME: 12.06.2023 current user is needed for these two
    @PostMapping("/change-password-link")
    public void changePassword(@RequestBody EmailDto emailDto) throws MessagingException {
        credentialsRenewalService.sendLetter(emailDto.getEmail(),
                emailContents.getPasswordChange(), false);
    }

    @PostMapping("/change-email-link")
    public void changeEmail(@RequestBody EmailDto emailDto) throws MessagingException {
        credentialsRenewalService.sendLetter(emailDto.getEmail(),
                emailContents.getEmailChange(), false);
    }

}

