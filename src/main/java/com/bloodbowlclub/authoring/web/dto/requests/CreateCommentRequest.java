package com.bloodbowlclub.authoring.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Le contenu du commentaire est requis")
    @Size(max = 1000, message = "Le contenu du commentaire ne peut pas dépasser 1000 caractères")
    private String content;
}