package com.bloodbowlclub.authoring.web.mappers;

import com.bloodbowlclub.authoring.domain.CommentEntity;
import com.bloodbowlclub.authoring.web.dto.responses.CommentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toDto(CommentEntity entity);

    List<CommentDto> toDtoList(List<CommentEntity> entities);
}