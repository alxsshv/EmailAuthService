package com.alxsshv.dto.mapper;

import com.alxsshv.dto.AccountDto;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    Account map(AccountDto dto);

    //@Mapping(target = "status", qualifiedByName = "statusToString", source = "status")
    AccountDto map(Account account);

    List<AccountDto> map(List<Account> accounts);

    @Named("statusToString")
    default String convertStatusToString(Status status) {
        return status.name();
    }
}
