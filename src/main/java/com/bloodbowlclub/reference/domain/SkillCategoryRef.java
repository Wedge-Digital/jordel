package com.bloodbowlclub.reference.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategoryRef {
    @NotNull
    @JsonProperty("id")
    private String uid;

    @NotNull
    private String label;
}
