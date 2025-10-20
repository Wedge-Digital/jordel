package com.auth.io.web.requests;

import com.auth.domain.user_account.commands.RegisterCommand;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegisterAccountMapper {

    RegisterAccountMapper INSTANCE = Mappers.getMapper( RegisterAccountMapper.class );

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    RegisterCommand requestToCommand(RegisterAccountRequest car);
}
