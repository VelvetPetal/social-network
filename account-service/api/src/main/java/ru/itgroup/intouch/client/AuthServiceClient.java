package ru.itgroup.intouch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itgroup.intouch.client.exceptionHandling.CustomErrorDecoder;
import ru.itgroup.intouch.dto.CaptchaDto;
import ru.itgroup.intouch.dto.EmailDto;
import ru.itgroup.intouch.dto.PasswordDto;
import ru.itgroup.intouch.dto.RegistrationDto;

@FeignClient(name = "auth-service",
        url = "${SN_ACCOUNT_HOST}" + ":" + "${SN_ACCOUNT_PORT}",
        path = "/api/v1/auth",
        configuration = {CustomErrorDecoder.class})
public interface AuthServiceClient {
    @PostMapping("/register")
    void register(@RequestBody RegistrationDto registrationDto);

    @PostMapping("/password/recovery/")
    void recoverPassword(@RequestBody EmailDto emailDto);

    @PostMapping("/password/recovery/{linkId}")
    void setNewPassword(@PathVariable String linkId,
                        @RequestBody PasswordDto passwordDto);

    @GetMapping("/captcha")
    CaptchaDto captcha();

    @PostMapping("/change-email-link")
    void changeEmail(@RequestBody EmailDto emailDto);

    @PostMapping("/change-password-link")
    void changePassword(@RequestBody EmailDto emailDto);
}
