package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.roster.*;

public class PlayerDefinitionCreator {

    public PlayerDefinition createWardancer() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("WOOD_ELVES__WARDANCER"))
                .name(new PlayerDefinitionName("Wardancer"))
                .price(new PlayerPrice(120))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createWoodElfLineman() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("WOOD_ELVES__LINEMAN"))
                .name(new PlayerDefinitionName("Wood Elf Lineman"))
                .price(new PlayerPrice(65))
                .maxQuantity(new PlayerMaxQuantity(16))
                .build();
    }

    public PlayerDefinition createHuman() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("HUMAN__LINEMAN"))
                .name(new PlayerDefinitionName("human"))
                .price(new PlayerPrice(100))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createWitchElf() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("DARK_ELVES__WITCH_ELF"))
                .name(new PlayerDefinitionName("Witch Elf"))
                .price(new PlayerPrice(110))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createBlitzer() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("DARK_ELVES__BLITZER"))
                .name(new PlayerDefinitionName("Dark elf blitzer"))
                .price(new PlayerPrice(100))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createAssassin() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("DARK_ELVES__ASSASSIN"))
                .name(new PlayerDefinitionName("Dark elf Assassin"))
                .price(new PlayerPrice(90))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createLineman() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("DARK_ELVES__LINEMAN"))
                .name(new PlayerDefinitionName("Dark elf lineman"))
                .price(new PlayerPrice(65))
                .maxQuantity(new PlayerMaxQuantity(16))
                .build();
    }

    public PlayerDefinition createMinotaur() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("CHAOS_CHOSEN__MINOTAUR"))
                .name(new PlayerDefinitionName("Minotaur"))
                .price(new PlayerPrice(150))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createOgre() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("CHAOS_CHOSEN__OGRE"))
                .name(new PlayerDefinitionName("Ogre"))
                .price(new PlayerPrice(140))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createRatOgre() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("SKAVEN__RAT_OGRE"))
                .name(new PlayerDefinitionName("Rat Ogre"))
                .price(new PlayerPrice(150))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createTroll() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("CHAOS_CHOSEN__TROLL"))
                .name(new PlayerDefinitionName("Troll"))
                .price(new PlayerPrice(150))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createProElfBlitzer() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("PRO_ELVES__BLITZER"))
                .name(new PlayerDefinitionName("Pro Elf Blitzer"))
                .price(new PlayerPrice(105))
                .maxQuantity(new PlayerMaxQuantity(4))
                .build();
    }

    public PlayerDefinition createZombie() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("UNDEAD__ZOMBIE"))
                .name(new PlayerDefinitionName("Zombie"))
                .price(new PlayerPrice(40))
                .maxQuantity(new PlayerMaxQuantity(16))
                .build();
    }

    public PlayerDefinition createGhoul() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("UNDEAD__GHOUL"))
                .name(new PlayerDefinitionName("Ghoul"))
                .price(new PlayerPrice(75))
                .maxQuantity(new PlayerMaxQuantity(4))
                .build();
    }

    public PlayerDefinition createRevenant() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("UNDEAD__REVENANT"))
                .name(new PlayerDefinitionName("Revenant"))
                .price(new PlayerPrice(90))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createMummy() {
        return PlayerDefinition.builder()
                .playerDefinitionID(new PlayerDefinitionID("UNDEAD__MUMMY"))
                .name(new PlayerDefinitionName("Mummy"))
                .price(new PlayerPrice(125))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }
}
