package ru.itgroup.intouch.service.search;

import dto.AccountSearchDto;
import dto.AccountSearchDtoPageable;
import filters.AccountFilterBuilder;
import lombok.RequiredArgsConstructor;
import mappers.AccountDtoPageableMapper;
import model.account.Account;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgroup.intouch.dto.AccountDto;
import ru.itgroup.intouch.repository.AccountRepository;
import ru.itgroup.intouch.repository.UserRepository;
import searchUtils.Filter;
import searchUtils.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountSearchService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountFilterBuilder filterBuilder;
    private final SpecificationBuilder specificationBuilder;
    private final AccountDtoPageableMapper dtoPageableMapper;
    private final ModelMapper modelMapper;
    private AccountSearchDto accountSearchDto;

    public Page<AccountDto> getAccountResponse(AccountSearchDtoPageable dto) {

        accountSearchDto = dtoPageableMapper.mapToAccountSearchDto(dto);
        Pageable pageable;
        if(dto.getSize() == null || dto.getPage() == null){
            pageable = PageRequest.of(0, 5);
        }else {
            pageable = dtoPageableMapper.mapToPageable(dto);
        }

        List<Long> authorIds = (!dto.getAuthor().isEmpty()) ? getUserIdsFromAuthor(dto.getAuthor()) : new ArrayList<>();

        List<Filter> filter = filterBuilder.createFilter(accountSearchDto, authorIds);

        if (filter.isEmpty()) {
            return null;
        }

        Specification<Account> specification = (Specification<Account>) specificationBuilder.getSpecificationFromFilters(filter);

        Page<Account> pageResult = accountRepository.findAll(specification, pageable);

        if (pageResult.hasContent()) {
            return pageResult.map(Account -> modelMapper.map(Account, AccountDto.class));
        } else {
            return null;
        }
    }

    private List<Long> getUserIdsFromAuthor(String author) {

        String[] authorNames = accountSearchDto.getAuthor().split(" ");

        List<Long> authors = userRepository.findAllByNames(authorNames);

        return new ArrayList<>(authors);


    }
}

