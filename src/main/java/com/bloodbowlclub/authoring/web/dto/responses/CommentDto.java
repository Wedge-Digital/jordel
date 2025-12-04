package com.bloodbowlclub.authoring.web.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private String authorName;
    private String authorPhotoUrl;
    private Instant createdAt;
    private String content;
}