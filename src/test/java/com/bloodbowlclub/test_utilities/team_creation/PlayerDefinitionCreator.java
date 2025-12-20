package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.*;

public class PlayerDefinitionCreator {

    public PlayerDefinition createWardancer() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCSHX7EPNSF162TQE59BHW3P"))
                .name(new PlayerDefinitionName("Wardancer"))
                .price(new PlayerPrice(120))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createWoodElfLineman() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCWNNJR6HXBS8MXVDVS7JZHM"))
                .name(new PlayerDefinitionName("Wood Elf Lineman"))
                .price(new PlayerPrice(65))
                .maxQuantity(new PlayerMaxQuantity(16))
                .build();
    }

    public PlayerDefinition createHuman() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCSGVRA218V8TNT1MRHT1P7K"))
                .name(new PlayerDefinitionName("human"))
                .price(new PlayerPrice(100))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createWitchElf() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCVWCDHJVZAW5XC1TZ96QDTZ"))
                .name(new PlayerDefinitionName("Witch Elf"))
                .price(new PlayerPrice(110))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createBlitzer() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCVWG6BCYNQXEZVZT6BA514M"))
                .name(new PlayerDefinitionName("Dark elf blitzer"))
                .price(new PlayerPrice(100))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createAssassin() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCVWJ188GT7MV9DNMZ73HK1F"))
                .name(new PlayerDefinitionName("Dark elf Assassin"))
                .price(new PlayerPrice(90))
                .maxQuantity(new PlayerMaxQuantity(2))
                .build();
    }

    public PlayerDefinition createLineman() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCVWJTB9J6D7NJNSPS81N296"))
                .name(new PlayerDefinitionName("Dark elf lineman"))
                .price(new PlayerPrice(65))
                .maxQuantity(new PlayerMaxQuantity(16))
                .build();
    }

    public PlayerDefinition createMinotaur() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCWGE6PAW5FMNCGD7EEKDTWF"))
                .name(new PlayerDefinitionName("Minotaur"))
                .price(new PlayerPrice(150))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createOgre() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCWGHV7EZSZFXVFSX108QB7D"))
                .name(new PlayerDefinitionName("Ogre"))
                .price(new PlayerPrice(140))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createRatOgre() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCWGK9PD5YPKVTWNCQNW1F9P"))
                .name(new PlayerDefinitionName("Rat Ogre"))
                .price(new PlayerPrice(150))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }

    public PlayerDefinition createTroll() {
        return PlayerDefinition.builder()
                .playerDefinitionID( new PlayerDefinitionID("01KCWGMQDTH9Q9WWJWSEYNV7GM"))
                .name(new PlayerDefinitionName("Troll"))
                .price(new PlayerPrice(115))
                .maxQuantity(new PlayerMaxQuantity(1))
                .build();
    }
}
