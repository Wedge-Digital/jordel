package com.bloodbowlclub.team_building.domain.team_staff.staff_detail;

import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class Cheerleaders extends TeamStaff {
}
