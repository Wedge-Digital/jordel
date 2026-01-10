package com.bloodbowlclub.reference.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Représente une limite croisée entre plusieurs positions de joueurs.
 */
@Getter
@AllArgsConstructor
public class CrossLimitRef {
    private final int maxQuantity;
    private final List<String> limitedPlayerUids;

    public boolean isEmpty() {
        return limitedPlayerUids == null || limitedPlayerUids.isEmpty();
    }

    public boolean appliesToPlayer(String playerUid) {
        return limitedPlayerUids != null && limitedPlayerUids.contains(playerUid);
    }

    public List<String> getLimitedPlayerUids() {
        return limitedPlayerUids != null ?
            Collections.unmodifiableList(limitedPlayerUids) :
            Collections.emptyList();
    }
}
