package com.bloodbowlclub.authoring.web.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphDto {
    private String title;
    private String content;
}