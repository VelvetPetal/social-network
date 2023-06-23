package ru.itgroup.intouch.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.account.Account;
import model.account.User;
import org.springframework.stereotype.Service;
import ru.itgroup.intouch.dto.AccountDto;
import ru.itgroup.intouch.dto.UserDto;
import ru.itgroup.intouch.exceptions.NoUserRegisteredException;
import ru.itgroup.intouch.mapper.UserMapper;
import ru.itgroup.intouch.repository.AccountRepository;
import ru.itgroup.intouch.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccountRepository accountRepository;


    public AccountDto getAccountInfo(String email) {
        Optional<Account> account = accountRepository
                .findFirstByEmailEqualsAndIsDeletedEquals(email, false);
        if (account.isEmpty()) {
            throw new NoUserRegisteredException("Аккаунт с адресом \"" +
                    email + "\" не найден");
        }
        return userMapper.accountEntityToAccountDto(account.get());
    }

    public UserDto getUserInfo(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new NoUserRegisteredException("Неверный логин или пароль");
        }
        Optional<User> user = userRepository
                .findFirstByEmailEqualsAndIsDeletedEquals(email, false);
        if (user.isPresent()) {
            return userMapper.userEntity2UserDto(user.get());
        }
        throw new NoUserRegisteredException("Пользователь с адресом \"" +
                email + "\" удалил свой аккаунт");
    }

    public void updateAccountData(AccountDto accountDto) {
        Optional<Account> account = accountRepository
                .findFirstByEmailEqualsAndIsDeletedEquals(accountDto.getEmail(), false);
        log.info("change account with email \"" + accountDto.getEmail() + "\"");
        if (account.isEmpty()) {
            throw new NoUserRegisteredException("Невозможно изменить данные аккаунта. E-mail \"" +
                    accountDto.getEmail() + "\" не найден");
        }
        Account accountEntity = account.get();
        userMapper.updateAccountFromDto(accountDto, accountEntity);
        accountEntity.setUpdateOn(LocalDateTime.now());
        accountRepository.save(accountEntity);
    }

    public void setAccountDeleted(String email) {
        Optional<User> firstByEmail = userRepository
                .findFirstByEmailEqualsAndIsDeletedEquals(email, false);
        if (firstByEmail.isEmpty()) {
            throw new NoUserRegisteredException("Невозможно удалить аккаунт. " +
                    "E-mail \"" + email + "\" не найден.");
        }
        User user = firstByEmail.get();
        user.setDeleted(true);
        userRepository.save(firstByEmail.get());
    }

    public List<AccountDto> getListOfUsers(List<Long> userIds) {
        List<Account> accounts = accountRepository.findByIdIn(userIds);
        return userMapper.accountsToDtos(accounts);
    }


    public AccountDto getAccountInfoById(Long id) {
        Optional<Account> account = accountRepository.findById(id);

        if(account.isEmpty()){
            throw new NoUserRegisteredException("Account not found");
        }
        return userMapper.accountEntityToAccountDto(account.get());
    }
}
