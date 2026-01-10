package com.bloodbowlclub.reference.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillRef {
    @NotNull
    private String uid;

    @NotNull
    private String name;

    @NotNull
    private String category;

    @NotNull
    private String type;

    @NotNull
    private String activation;

    private String description;
}
