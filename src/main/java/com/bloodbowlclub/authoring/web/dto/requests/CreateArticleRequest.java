package com.bloodbowlclub.authoring.web.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {

    @NotBlank(message = "Le titre est requis")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;

    @NotBlank(message = "La description est requise")
    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères")
    private String description;

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    @NotBlank(message = "Le paragraphe de tête est requis")
    @Size(max = 10000, message = "Le paragraphe de tête ne peut pas dépasser 10000 caractères")
    private String leadParagraph;

    @Valid
    private List<ParagraphRequest> paragraphs = new ArrayList<>();
}