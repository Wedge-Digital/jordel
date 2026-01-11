package com.bloodbowlclub.team_building.io.web.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterNewTeamRequest {

    @NotBlank(message = "{team.id.required}")
    private String teamId;

    @NotBlank(message = "{team.name.required}")
    @Size(min = 3, max = 100, message = "{team.name.size}")
    private String teamName;

    @NotBlank(message = "{team.logo.required}")
    private String teamLogo;

    @NotBlank(message = "{team.id.required}")
    @Size(min = 26, max = 26, message="invlaid ULID")
    private String coachId;
}