package com.alxsshv.dto.mapper;

import com.alxsshv.dto.AccountDto;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.Status;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    

    @Mapping(target = "status", qualifiedByName = "statusToString")
    AccountDto mapToAccountDto(Account account);

    List<AccountDto> mapToAccountsList(List<Account> accounts);

    @Named("statusToString")
    default String convertStatusToString(Status status) {
        return status.name();
    }

}
