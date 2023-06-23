package ru.itgroup.intouch.mapper;


import model.account.Account;
import model.account.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.itgroup.intouch.dto.AccountDto;
import ru.itgroup.intouch.dto.RegistrationDto;
import ru.itgroup.intouch.dto.UserDto;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto userEntity2UserDto(User entity);

    @Mapping(target = "password", source = "password1")
    Account registrationDto2AccountEntity(RegistrationDto dto);

    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    AccountDto accountEntityToAccountDto(Account entity);

    List<AccountDto> accountsToDtos(List<Account> accountList);
    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "hashExpiryTime", ignore = true)
    void updateAccountFromDto(AccountDto dto, @MappingTarget Account entity);


}
