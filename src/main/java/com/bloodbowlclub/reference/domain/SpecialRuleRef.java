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
public class SpecialRuleRef {
    @NotNull
    private String uid;

    @NotNull
    private String label;
}
