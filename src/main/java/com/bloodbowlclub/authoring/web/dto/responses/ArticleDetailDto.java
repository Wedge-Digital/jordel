package com.bloodbowlclub.authoring.web.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailDto {
    private String id;
    private String title;
    private String authorName;
    private String authorPhotoUrl;
    private String description;
    private String imageUrl;
    private Instant createdAt;
    private String leadParagraph;
    private List<ParagraphDto> paragraphs;
    private List<CommentDto> comments;
}