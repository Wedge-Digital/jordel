package com.bloodbowlclub.ruleset_building;

import com.bloodbowlclub.reference.service.ReferenceDataService;
import com.bloodbowlclub.ruleset_building.domain.RulesetTier;
import com.bloodbowlclub.ruleset_building.domain.Ruleset;
import com.bloodbowlclub.ruleset_building.domain.SkillCost;
import com.bloodbowlclub.ruleset_building.domain.SkillGroup;
import com.bloodbowlclub.shared.inducement.InducementID;
import com.bloodbowlclub.shared.inducement.StarplayerID;
import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.ruleset.CreationBudget;
import com.bloodbowlclub.shared.ruleset.RulesetID;
import com.bloodbowlclub.shared.ruleset.RulesetName;
import com.bloodbowlclub.shared.skills.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.List;
import java.util.function.Predicate;

public class RulesetTest {

    ResourceLoader resourceLoader = new DefaultResourceLoader();

    ReferenceDataService refService = new ReferenceDataService(resourceLoader);

    List<StarplayerID> veterans = List.of(
        new StarplayerID("AKHORNE_THE_SQUIRREL"),
        new StarplayerID("ANQI_PANQI"),
        new StarplayerID("BARIK_FARBLAST"),
        new StarplayerID("BILEROT_VOMITFLESH"),
        new StarplayerID("BOA_KONSSSTRIKTR"),
        new StarplayerID("CAPTAIN_KARINA_VON_RIESZ"),
        new StarplayerID("COUNT_LUTHOR_VON_DRAKENBORG"),
        new StarplayerID("ELDRIL_SIDEWINDER"),
        new StarplayerID("FUNGUS_THE_LOON"),
        new StarplayerID("GLART_SMASHRIP"),
        new StarplayerID("GLORIEL_SUMMERBLOOM"),
        new StarplayerID("GLOTL_STOP"),
        new StarplayerID("GRASHNAK_BLACKHOOF"),
        new StarplayerID("GRETCHEN_WACHTER"),
        new StarplayerID("GRIM_IRONJAW"),
        new StarplayerID("GROMBRINDAL"),
        new StarplayerID("GUFFLE_PUSMAW"),
        new StarplayerID("HELMUT_WULF"),
        new StarplayerID("JOSEF_BUGMAN"),
        new StarplayerID("KARLA_VON_KILL"),
        new StarplayerID("KIROTH_KRAKENEYE"),
        new StarplayerID("MAX_SPLEENRIPPER"),
        new StarplayerID("NOBBLA_BLACKWART"),
        new StarplayerID("PUGGY_BACONBREATH"),
        new StarplayerID("RASHNAK_BACKSTABBER"),
        new StarplayerID("RODNEY_ROACHBAIT"),
        new StarplayerID("ROWANA_FORESTFOOT"),
        new StarplayerID("RUMBELOW_SHEEPSKIN"),
        new StarplayerID("SCRAPPA_SOREHEAD"),
        new StarplayerID("SCYLA_ANFINGRIMM"),
        new StarplayerID("SKRULL_HALFHEIGHT"),
        new StarplayerID("SWIFTVINE_GLIMMERSHARD"),
        new StarplayerID("THE_BLACK_GOBBO"),
        new StarplayerID("THE_MIGHTY_ZUG"),
        new StarplayerID("THORSSON_STOUTMEAD"),
        new StarplayerID("VARAG_GHOUL_CHEWER"),
        new StarplayerID("WILHELM_CHANEY"),
        new StarplayerID("WILLOW_ROSEBARK"),
        new StarplayerID("WITHERGRASP_DOUBLEDROOL"),
        new StarplayerID("ZZHARG_MADEYE")
    );

    List<StarplayerID> legends = List.of(
            new StarplayerID("DEEPROOT_STRONGBRANCH"),
            new StarplayerID("GRAK_AND_CRUMBLEBERRY"),
            new StarplayerID("IVAR_ERIKSSON"),
            new StarplayerID("KREEK_RUSTGOUGER"),
            new StarplayerID("LORD_BORAK_THE_DESPOILER"),
            new StarplayerID("LORD_BORAK_THE_DESPOILER"),
            new StarplayerID("THE_SWIFT_TWINS"),
            new StarplayerID("MAPLE_HIGHGROVE"),
            new StarplayerID("RIPPER_BOLGROT"),
            new StarplayerID("SKITTER_STAB_STAB"),
            new StarplayerID("SKRORG_SNOWPELT"),
            new StarplayerID("ZOLCATH_THE_ZOAT")
    );

    Predicate<Skill> eliteSkillsPredicate = Skill::isElite;
    Predicate<Skill> standarSkillsPredicate = Skill::isStandard;

    private void given_eurobowl_2026_ruleset() {
        List<InducementID> inducements = List.of(
                new InducementID("DODGY_LEAGUE_REP"),
                new InducementID("BLITZERS_BEST_KEGS"),
                new InducementID("BRIBES"),
                new InducementID("HALFLING_MASTER_CHEF"),
                new InducementID("MORTUARY_ASSISTANT"),
                new InducementID("PLAGUE_DOCTOR"),
                new InducementID("RIOTOUS_ROOKIES"),
                new InducementID("TEAM_MASCOT"),
                new InducementID("WANDERING_APOTHECARY"),
                new InducementID("WEATHER_MAGE")
        );

        RulesetTier tier1 = RulesetTier.builder()
                .budget(new CreationBudget(1060))
                .skillBudget(new CreationBudget(120))
                .additionalBudget(new CreationBudget(10))
                .allowedStarPlayers(null)
                .allowedInducements(inducements)
                .rosterList(List.of(new RosterID("AMAZON"),
                        new RosterID("OLD_WORLD_ALLIANCE"),
                        new RosterID("WOOD_ELF"))).build();

        RulesetTier tier2 = RulesetTier.builder()
                .budget(new CreationBudget(1070))
                .skillBudget(new CreationBudget(140))
                .additionalBudget(new CreationBudget(20))
                .allowedStarPlayers(null)
                .allowedInducements(inducements)
                .rosterList(List.of(new RosterID("NORSE"),
                        new RosterID("ORC"),
                        new RosterID("SHAMBLING_UNDEAD"),
                        new RosterID("SKAVEN"),
                        new RosterID("UNDERWORLD_DENIZENS")
                )).build();

        RulesetTier tier3 = RulesetTier.builder()
                .budget(new CreationBudget(1080))
                .skillBudget(new CreationBudget(160))
                .additionalBudget(new CreationBudget(30))
                .allowedStarPlayers(null)
                .allowedInducements(inducements)
                .rosterList(List.of(new RosterID("DARK_ELF"),
                        new RosterID("DWARF"),
                        new RosterID("HUMAN"),
                        new RosterID("LIZARDMEN"),
                        new RosterID("NECROMANTIC_HORROR"),
                        new RosterID("VAMPIRE")
                )).build();

        RulesetTier tier4 = RulesetTier.builder()
                .budget(new CreationBudget(1100))
                .skillBudget(new CreationBudget(190))
                .additionalBudget(new CreationBudget(30))
                .allowedStarPlayers(null)
                .allowedInducements(inducements)
                .rosterList(List.of(new RosterID("CHAOS_CHOSEN"),
                        new RosterID("CHAOS_DWARF"),
                        new RosterID("CHAOS_RENEGADE"),
                        new RosterID("ELVEN_UNION"),
                        new RosterID("HIGH_ELF"),
                        new RosterID("IMPERIAL_NOBILITY"),
                        new RosterID("NURGLE"),
                        new RosterID("TOMB_KINGS")
                )).build();

//        RulesetTier tier5 = RulesetTier.builder()
//                .budget(new CreationBudget(1120))
//                .skillBudget(new CreationBudget(220))
//                .additionalBudget(new CreationBudget(30))
//                .allowedStarPlayers(List.of(veterans, legends))
//                .allowedInducements(inducements)
//                .rosterList(List.of(new RosterID("BLACK_ORC"),
//                        new RosterID("BRETONNIAN"),
//                        new RosterID("GOBLIN"),
//                        new RosterID("KHORNE"),
//                        new RosterID("SNOTLING")
//                )).build();
//
//        RulesetTier tier6 = RulesetTier.builder()
//                .budget(new CreationBudget(1140))
//                .skillBudget(new CreationBudget(240))
//                .additionalBudget(new CreationBudget(40))
//                .allowedStarPlayers(List.of(veterans, legends))
//                .allowedInducements(inducements)
//                .rosterList(List.of(new RosterID("GNOME"),
//                        new RosterID("HALFLING"),
//                        new RosterID("OGRE")
//                )).build();

        SkillGroup standard = SkillGroup.builder()
                .primaryCost(new SkillCost(20))
                .secondaryCost(new SkillCost(40))
                .skillSelector(standarSkillsPredicate)
                .build();

        SkillGroup elite = SkillGroup.builder()
                .primaryCost(new SkillCost(30))
                .secondaryCost(new SkillCost(50))
                .skillSelector(eliteSkillsPredicate)
                .build();

//        RuleSetValidator rsValidator = RuleSetValidator.builder()
//                .RulesetValidationRules(List.of(
//                ))
//
//        StackCost standardToStandard = StackCost.Builder()
//                .firstSkillgroup(standard)
//                .secondSkillGroup(standard)
//                .stackCost(new SkillCost(10))
//
//        StackCostRules skCost = StackCostRules.builder()
//                .stack2PrimaryStandard(new SkillCost(50))
//                .stack2PrimaryEliteAndStandard(new SkillCost(60))
//                .stack2PrimaryElite(new SkillCost(70))
//                .build();
//
//        List<Inducement> inducements = List.of(
//
//        );
//
//        Ruleset euro2026Ruleset = Ruleset.builder()
//                .id(new RulesetID("EURO_2026"))
//                .name(new RulesetName("Euro 2026 ruleset v1.1"))
//                .tierList(List.of(tier1, tier2, tier3, tier4, tier5, tier6))
//
//    }
//
//    @Test
//    @DisplayName("Euro 2026 ruleset, shall be buildable")
//    void ruleset_shall_contain_at_least_one_tier_list() {
//        given_eurobowl_2026_ruleset();
//        then_ruleset_shall_be_valid();
//    }

//    @Test
//    @DisplayName("StressCup 2026 ruleset, shall be buildable")
//    void stress_cup_2026_shall_be_buildable() {
//        // TODO: Complete this test when Ruleset builder is finalized
//        // refService.loadAllReferenceData();
//
//        // Tier tier1 = Tier.builder()
//        //         .rosterList(refService.getAllRosters(Locale.getDefault()))
//        //         .skillRules()
//        //         .build();
//        // Ruleset ruleset = Ruleset.builder()
//        //         .id(new RulesetID("StressCup2026"))
//        //         .tierList()
//        //         .build();
    }

}
