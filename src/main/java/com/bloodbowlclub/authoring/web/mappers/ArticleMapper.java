package com.bloodbowlclub.authoring.web.mappers;

import com.bloodbowlclub.authoring.domain.ArticleEntity;
import com.bloodbowlclub.authoring.domain.ParagraphEntity;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleDetailDto;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleSummaryDto;
import com.bloodbowlclub.authoring.web.dto.responses.ParagraphDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleSummaryDto toSummaryDto(ArticleEntity entity);

    List<ArticleSummaryDto> toSummaryDtoList(List<ArticleEntity> entities);

    @Mapping(target = "paragraphs", source = "paragraphs")
    @Mapping(target = "comments", source = "comments")
    ArticleDetailDto toDetailDto(ArticleEntity entity);

    ParagraphDto toParagraphDto(ParagraphEntity entity);

    List<ParagraphDto> toParagraphDtoList(List<ParagraphEntity> entities);
}