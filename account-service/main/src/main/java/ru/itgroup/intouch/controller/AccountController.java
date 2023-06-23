package ru.itgroup.intouch.controller;

import dto.AccountSearchDtoPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itgroup.intouch.dto.AccountDto;
import ru.itgroup.intouch.dto.UserDto;
import ru.itgroup.intouch.service.account.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final ru.itgroup.intouch.service.search.AccountSearchService accountSearchService;

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable("id") String id){
        return accountService.getAccountInfoById(Long.valueOf(id));
    }

    @PostMapping("/accounts")
    public List<AccountDto> accounts(@RequestBody List<Long> userIds) {
        return accountService.getListOfUsers(userIds);
    }

    @GetMapping("/currentUser")
    UserDto currentUser(@RequestParam("email") String email) {
        return accountService.getUserInfo(email);
    }

    @GetMapping("/me")
    public AccountDto myAccount(@RequestParam("email") String email) {
        return accountService.getAccountInfo(email);
    }

    @PutMapping("/me")
    public AccountDto changeProfileInfo(@RequestBody AccountDto accountDto) {
        accountService.updateAccountData(accountDto);
        return accountDto;
    }

    @DeleteMapping("/me")
    public void deleteAccount(@RequestParam("email") String email) {
        accountService.setAccountDeleted(email);
    }

    @PostMapping("/search")
    public Page<AccountDto> search(@RequestBody AccountSearchDtoPageable dto) {
        return accountSearchService.getAccountResponse(dto);

    }

}
