package ru.itgroup.intouch.client;

import dto.AccountSearchDtoPageable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itgroup.intouch.client.exceptionHandling.CustomErrorDecoder;
import ru.itgroup.intouch.dto.AccountDto;
import ru.itgroup.intouch.dto.UserDto;

import java.util.List;


@FeignClient(name = "account-service",
        url = "${SN_ACCOUNT_HOST}" + ":" + "${SN_ACCOUNT_PORT}",
        path = "/api/v1/account",
        configuration = {CustomErrorDecoder.class})
public interface AccountServiceClient {
    @GetMapping("/me")
    AccountDto myAccount(@RequestParam("email") String email);

    @GetMapping("/currentUser")
    UserDto currentUser(@RequestParam("email") String email);

    @PutMapping("/me")
    AccountDto changeProfile(@RequestBody AccountDto accountDto);

    @DeleteMapping("/me")
    void deleteAccount(@RequestParam("email") String email);

    @GetMapping("/search")
    Page<AccountDto> search(AccountSearchDtoPageable dto);

    @PostMapping("/accounts")
    List<AccountDto> accounts(@RequestBody List<Long> userIds);

}