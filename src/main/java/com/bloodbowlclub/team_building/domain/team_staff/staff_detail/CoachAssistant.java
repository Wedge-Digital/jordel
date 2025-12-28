package com.bloodbowlclub.team_building.domain.team_staff.staff_detail;

import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class CoachAssistant extends TeamStaff {
}
