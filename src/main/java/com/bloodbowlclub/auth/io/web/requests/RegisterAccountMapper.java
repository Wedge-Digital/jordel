package com.bloodbowlclub.auth.io.web.requests;

import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegisterAccountMapper {

    RegisterAccountMapper INSTANCE = Mappers.getMapper( RegisterAccountMapper.class );

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    RegisterAccountCommand requestToCommand(RegisterAccountRequest car);
}
