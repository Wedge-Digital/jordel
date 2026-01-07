package com.bloodbowlclub.team_building.io.web.requests;

import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegisterNewTeamMapper {

    RegisterNewTeamMapper INSTANCE = Mappers.getMapper(RegisterNewTeamMapper.class);

    @Mapping(source = "teamId", target = "teamId")
    @Mapping(source = "teamName", target = "teamName")
    @Mapping(source = "teamLogo", target = "teamLogo")
    RegisterNewTeamCommand requestToCommand(RegisterNewTeamRequest request);
}
