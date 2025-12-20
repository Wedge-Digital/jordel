package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class Roster extends AggregateRoot {

    @Valid
    @NotNull
    @JsonIgnore
    private RosterID rosterId;

    @Valid
    @NotNull
    private RosterName name;

    @NotEmpty
    @Valid
    private List<PlayerDefinition> playerDefinitions;

    @Override
    public String getId() {
        return rosterId.toString();
    }

    private CrossLimit crossLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roster roster = (Roster) o;
        return this.rosterId.equals(roster.rosterId);
        // ou une autre logique (name, etc.) selon ce qui définit l’égalité
    }

    @Override
    public int hashCode() {
        return Objects.hash(rosterId.toString());
    }

    public boolean hasNoCrossLimits() {
        return this.crossLimit == null;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    boolean doesNotContainPlayer(PlayerDefinition player) {
        if (this.playerDefinitions == null || this.playerDefinitions.isEmpty()) {
            return true;
        }
        return this.playerDefinitions.stream().filter(lines -> lines.equals(player)).toList().isEmpty();
    }


    //===============================================================================================================
    //
    // Application d'évènements
    //
    //===============================================================================================================


}
