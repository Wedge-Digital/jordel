#!/usr/bin/env python3
"""
Script pour ajouter les leagues aux rosters dans les fichiers teams_*.json
"""
import json
import sys

# Mapping des rosters vers leurs leagues
ROSTER_LEAGUES = {
    "AMAZON": ["LUSTRIAN_SUPERLEAGUE"],
    "BLACK ORC": ["BADLANDS_BRAWL"],
    "BRETONNIAN": ["OLD_WORLD_CLASSIC"],
    "CHAOS CHOSEN": ["CHAOS_CLASH"],
    "CHAOS DWARF": ["BADLANDS_BRAWL", "CHAOS_CLASH"],
    "CHAOS RENEGADE": ["CHAOS_CLASH"],
    "DARK ELF": ["ELVEN_KINGDOMS_LEAGUE"],
    "DWARF": ["WORLDS_EDGE_SUPERLEAGUE"],
    "ELVEN UNION": ["ELVEN_KINGDOMS_LEAGUE"],
    "GNOME": ["HALFLING_THIMBLE_CUP", "WOODLAND_LEAGUE"],
    "GOBLIN": ["BADLANDS_BRAWL", "UNDERWORLD_CHALLENGE"],
    "HALFLING": ["HALFLING_THIMBLE_CUP", "WOODLAND_LEAGUE"],
    "HUMAN": ["OLD_WORLD_CLASSIC"],
    "IMPERIAL NOBILITY": ["OLD_WORLD_CLASSIC"],
    "KHORNE": ["CHAOS_CLASH"],
    "LIZARDMAN": ["LUSTRIAN_SUPERLEAGUE"],
    "NECROMANTIC HORROR": ["SYLVANIAN_SPOTLIGHT"],
    "NORSE": ["CHAOS_CLASH", "OLD_WORLD_CLASSIC"],
    "NURGLE": ["CHAOS_CLASH"],
    "OGRE": ["BADLANDS_BRAWL", "WORLDS_EDGE_SUPERLEAGUE"],
    "OLD WORLD ALLIANCE": ["OLD_WORLD_CLASSIC"],
    "ORC": ["BADLANDS_BRAWL"],
    "SHAMBLING UNDEAD": ["SYLVANIAN_SPOTLIGHT"],
    "SKAVEN": ["UNDERWORLD_CHALLENGE"],
    "SNOTLING": ["UNDERWORLD_CHALLENGE"],
    "TOMB KINGS": ["SYLVANIAN_SPOTLIGHT"],
    "UNDERWORLD DENIZENS": ["UNDERWORLD_CHALLENGE"],
    "VAMPIRE": ["SYLVANIAN_SPOTLIGHT"],
    "WOOD ELF": ["ELVEN_KINGDOMS_LEAGUE", "WOODLAND_LEAGUE"],
    "HIGH ELF": ["ELVEN_KINGDOMS_LEAGUE"],
}

def add_leagues_to_teams(input_file, output_file):
    """Ajoute les leagues aux rosters dans le fichier JSON"""

    # Charger le fichier JSON
    with open(input_file, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # Parcourir les équipes et ajouter les leagues
    teams_updated = 0
    teams_not_found = []

    for team in data.get('teams', []):
        uid = team.get('uid', '')

        # Chercher le mapping (par uid directement ou par nom converti)
        leagues = None

        # Essayer avec l'UID tel quel
        if uid in ROSTER_LEAGUES:
            leagues = ROSTER_LEAGUES[uid]
        # Essayer avec le nom de l'équipe
        elif team.get('name', '') in ROSTER_LEAGUES:
            leagues = ROSTER_LEAGUES[team['name']]

        if leagues:
            team['leagues'] = leagues
            teams_updated += 1
            print(f"✅ {uid}: {leagues}")
        else:
            teams_not_found.append(uid)
            print(f"⚠️  {uid}: No league mapping found")
            # Ajouter une liste vide par défaut
            team['leagues'] = []

    # Sauvegarder le fichier JSON avec indentation
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

    print(f"\n📊 Résumé:")
    print(f"  - Équipes mises à jour: {teams_updated}")
    print(f"  - Équipes non trouvées: {len(teams_not_found)}")
    if teams_not_found:
        print(f"  - UIDs non mappés: {', '.join(teams_not_found)}")
    print(f"  - Fichier sauvegardé: {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python3 add_leagues_to_teams.py <input_file> <output_file>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    add_leagues_to_teams(input_file, output_file)
