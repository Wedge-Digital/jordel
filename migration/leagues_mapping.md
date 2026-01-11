# Mapping Rosters -> Leagues (Blood Bowl 2025)

## Leagues créées

1. **LUSTRIAN_SUPERLEAGUE** - Lustrian Superleague
2. **BADLANDS_BRAWL** - Badlands Brawl
3. **OLD_WORLD_CLASSIC** - Old World Classic
4. **CHAOS_CLASH** - Chaos Clash
5. **ELVEN_KINGDOMS_LEAGUE** - Elven Kingdoms League
6. **WORLDS_EDGE_SUPERLEAGUE** - Worlds Edge Superleague
7. **HALFLING_THIMBLE_CUP** - Halfling Thimble Cup
8. **WOODLAND_LEAGUE** - Woodland League
9. **UNDERWORLD_CHALLENGE** - Underworld Challenge
10. **SYLVANIAN_SPOTLIGHT** - Sylvanian Spotlight

## Mapping Rosters -> Leagues

| Roster UID (actuel) | Roster Name | League(s) UIDs |
|---------------------|-------------|----------------|
| AMAZON | Amazon | LUSTRIAN_SUPERLEAGUE |
| BLACK_ORC | Black Orc | BADLANDS_BRAWL |
| BRETONNIAN | Bretonnian | OLD_WORLD_CLASSIC |
| CHAOS_CHOSEN | Chaos Chosen | CHAOS_CLASH |
| CHAOS_DWARF | Chaos Dwarf | BADLANDS_BRAWL, CHAOS_CLASH |
| CHAOS_RENEGADE | Chaos Renegade | CHAOS_CLASH |
| DARK_ELF | Dark Elf | ELVEN_KINGDOMS_LEAGUE |
| DWARF | Dwarf | WORLDS_EDGE_SUPERLEAGUE |
| ELVEN_UNION | Elven Union | ELVEN_KINGDOMS_LEAGUE |
| GNOME | Gnome | HALFLING_THIMBLE_CUP, WOODLAND_LEAGUE |
| GOBLIN | Goblin | BADLANDS_BRAWL, UNDERWORLD_CHALLENGE |
| HALFLING | Halfling | HALFLING_THIMBLE_CUP, WOODLAND_LEAGUE |
| HUMAN | Human | OLD_WORLD_CLASSIC |
| IMPERIAL_NOBILITY | Imperial Nobility | OLD_WORLD_CLASSIC |
| KHORNE | Khorne | CHAOS_CLASH |
| LIZARDMAN | Lizardman | LUSTRIAN_SUPERLEAGUE |
| NECROMANTIC_HORROR | Necromantic Horror | SYLVANIAN_SPOTLIGHT |
| NORSE | Norse | CHAOS_CLASH, OLD_WORLD_CLASSIC |
| NURGLE | Nurgle | CHAOS_CLASH |
| OGRE | Ogre | BADLANDS_BRAWL, WORLDS_EDGE_SUPERLEAGUE |
| OLD_WORLD_ALLIANCE | Old World Alliance | OLD_WORLD_CLASSIC |
| ORC | Orc | BADLANDS_BRAWL |
| SHAMBLING_UNDEAD | Shambling Undead | SYLVANIAN_SPOTLIGHT |
| SKAVEN | Skaven | UNDERWORLD_CHALLENGE |
| SNOTLING | Snotling | UNDERWORLD_CHALLENGE |
| TOMB_KINGS | Tomb Kings | SYLVANIAN_SPOTLIGHT |
| UNDERWORLD_DENIZENS | Underworld Denizens | UNDERWORLD_CHALLENGE |
| VAMPIRE | Vampire | SYLVANIAN_SPOTLIGHT |
| WOOD_ELF | Wood Elf | ELVEN_KINGDOMS_LEAGUE, WOODLAND_LEAGUE |
| HIGH_ELF | High Elf | ELVEN_KINGDOMS_LEAGUE |

## Notes

- **Plusieurs leagues** : Certaines équipes peuvent appartenir à plusieurs leagues (ex: Chaos Dwarf, Norse, Ogre, etc.)
- **Format** : Dans le JSON, les leagues seront stockées comme une liste de UIDs : `"leagues": ["BADLANDS_BRAWL", "CHAOS_CLASH"]`
- **Traductions** : Les noms de leagues restent en anglais même dans la version française (convention de la communauté Blood Bowl)

## Prochaines étapes

1. ✅ Créer les fichiers `leagues_en.json` et `leagues_fr.json`
2. ⏳ Créer la classe `LeagueRef` dans le domain
3. ⏳ Mettre à jour `RosterRef` pour ajouter `List<LeagueRef> leagues`
4. ⏳ Mettre à jour `ReferenceDataService` pour charger et résoudre les leagues
5. ⏳ Mettre à jour les fichiers `teams_en.json` et `teams_fr.json` avec les UIDs de leagues
6. ⏳ Ajouter des tests pour vérifier la résolution des leagues
