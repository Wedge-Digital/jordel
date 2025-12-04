package com.bloodbowlclub.authoring.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphRequest {

    @NotBlank(message = "Le titre du paragraphe est requis")
    @Size(max = 255, message = "Le titre du paragraphe ne peut pas dépasser 255 caractères")
    private String title;

    @NotBlank(message = "Le contenu du paragraphe est requis")
    @Size(max = 10000, message = "Le contenu du paragraphe ne peut pas dépasser 10000 caractères")
    private String content;
}