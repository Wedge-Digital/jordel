#!/usr/bin/env python3
"""
Script d'analyse préliminaire pour la migration des articles de blog
depuis l'ancien système Django/MySQL vers le nouveau système Authoring.

Génère un rapport détaillé des données à migrer et identifie les problèmes potentiels.
"""

import re
import csv
from datetime import datetime
from pathlib import Path
from collections import defaultdict
from dataclasses import dataclass
from typing import List, Dict, Optional

# Configuration
DUMP_FILE = "DataMigration/BBClub-2025_12_05_12_55_43-dump.sql"
REPORT_FILE = "migration/pre-migration-report.md"
CSV_ARTICLES_FILE = "migration/articles-analysis.csv"

# Limites de la structure cible
MAX_TITLE_LENGTH = 255
MAX_DESCRIPTION_LENGTH = 200
MAX_COMMENT_LENGTH = 1000


@dataclass
class Article:
    """Représente un article du blog"""
    id: int
    title: str
    slug: str
    description: str
    content: str
    featured_image: Optional[str]
    author_username: str
    author_email: str
    publish_date: Optional[str]
    comments_count: int
    status: int

    @property
    def title_too_long(self) -> bool:
        return len(self.title) > MAX_TITLE_LENGTH

    @property
    def description_too_long(self) -> bool:
        return len(self.description) > MAX_DESCRIPTION_LENGTH

    @property
    def has_external_image(self) -> bool:
        return bool(self.featured_image and
                   (self.featured_image.startswith('http://') or
                    self.featured_image.startswith('https://')))

    @property
    def is_published(self) -> bool:
        return self.status == 2


class Statistics:
    """Statistiques de migration"""
    def __init__(self):
        self.total_articles = 0
        self.published_articles = 0
        self.draft_articles = 0
        self.articles_with_comments = 0
        self.articles_with_images = 0
        self.articles_with_external_images = 0
        self.titles_too_long = 0
        self.descriptions_too_long = 0
        self.author_count = defaultdict(int)
        self.content_lengths = []
        self.problem_articles = []


def parse_sql_value(value: str) -> str:
    """Parse une valeur SQL (gère les quotes et échappements)"""
    if value == 'NULL':
        return None

    value = value.strip()
    if value.startswith("'") and value.endswith("'"):
        value = value[1:-1]

    # Unescape
    value = value.replace("\\'", "'")
    value = value.replace('\\"', '"')
    value = value.replace("\\n", "\n")
    value = value.replace("\\r", "\r")
    value = value.replace("\\\\", "\\")

    return value


def split_sql_values(values_str: str) -> List[str]:
    """
    Découpe une ligne de valeurs SQL en respectant les parenthèses et quotes.
    Exemple: (1,'test','desc') -> ['1', "'test'", "'desc'"]
    """
    result = []
    current = []
    in_quotes = False
    escape_next = False
    paren_depth = 0

    for char in values_str:
        if escape_next:
            current.append(char)
            escape_next = False
            continue

        if char == '\\':
            current.append(char)
            escape_next = True
            continue

        if char == "'" and not escape_next:
            in_quotes = not in_quotes
            current.append(char)
            continue

        if char == '(' and not in_quotes:
            paren_depth += 1
            current.append(char)
            continue

        if char == ')' and not in_quotes:
            paren_depth -= 1
            current.append(char)
            continue

        if char == ',' and not in_quotes and paren_depth == 0:
            result.append(''.join(current).strip())
            current = []
            continue

        current.append(char)

    if current:
        result.append(''.join(current).strip())

    return result


def extract_insert_values(line: str) -> List[List[str]]:
    """Extrait les valeurs d'une ligne INSERT INTO"""
    # Trouver la partie VALUES (...)
    match = re.search(r'VALUES\s+(.+);?$', line, re.DOTALL)
    if not match:
        return []

    values_part = match.group(1).rstrip(';')

    # Extraire chaque tuple (...)
    tuples = []
    current_tuple = []
    in_tuple = False
    in_quotes = False
    escape_next = False

    for char in values_part:
        if escape_next:
            current_tuple.append(char)
            escape_next = False
            continue

        if char == '\\':
            current_tuple.append(char)
            escape_next = True
            continue

        if char == "'":
            in_quotes = not in_quotes
            current_tuple.append(char)
            continue

        if char == '(' and not in_quotes:
            in_tuple = True
            continue

        if char == ')' and not in_quotes:
            if in_tuple:
                tuple_str = ''.join(current_tuple)
                values = split_sql_values(tuple_str)
                tuples.append(values)
                current_tuple = []
                in_tuple = False
            continue

        if in_tuple:
            current_tuple.append(char)

    return tuples


def parse_users(dump_path: Path) -> Dict[int, tuple]:
    """Parse les utilisateurs depuis le dump"""
    users = {}

    print("   📝 Parsing users...")
    with open(dump_path, 'r', encoding='utf-8', errors='ignore') as f:
        in_user_insert = False
        buffer = []
        line_count = 0

        for line_num, line in enumerate(f, 1):
            if line_num % 10000 == 0:
                print(f"\r   Processing line: {line_num:,}", end='', flush=True)

            if 'INSERT INTO `auth_user` VALUES' in line:
                in_user_insert = True
                buffer = [line]
            elif in_user_insert:
                buffer.append(line)
                # Continue jusqu'au point-virgule final
                if line.rstrip().endswith(';'):
                    full_insert = ''.join(buffer)
                    tuples = extract_insert_values(full_insert)

                    for values in tuples:
                        if len(values) >= 8:
                            try:
                                # Structure auth_user: id, password, last_login, is_superuser, username, first_name, last_name, email, ...
                                user_id = int(parse_sql_value(values[0]))
                                username = parse_sql_value(values[4])  # Colonne 4 = username
                                first_name = parse_sql_value(values[5])
                                last_name = parse_sql_value(values[6])
                                email = parse_sql_value(values[7])

                                # Créer un nom complet si disponible, sinon utiliser username
                                if first_name and last_name:
                                    display_name = f"{first_name} {last_name}".strip()
                                elif first_name:
                                    display_name = first_name
                                elif last_name:
                                    display_name = last_name
                                else:
                                    display_name = username

                                users[user_id] = (display_name, email)
                                line_count += 1
                            except (ValueError, IndexError):
                                pass

                    in_user_insert = False
                    buffer = []

    print(f"\r   ✓ Found {len(users)} users ({line_count:,} records parsed)")
    return users


def parse_articles(dump_path: Path, users: Dict[int, tuple]) -> List[Article]:
    """Parse les articles depuis le dump"""
    articles = []
    articles_by_id = {}  # Pour dédupliquer les articles (le dump peut avoir des duplicatas)

    print("   📝 Parsing articles...")
    with open(dump_path, 'r', encoding='utf-8', errors='ignore') as f:
        in_article_insert = False
        buffer = []
        parsed_count = 0

        for line_num, line in enumerate(f, 1):
            if line_num % 10000 == 0:
                print(f"\r   Processing line: {line_num:,} | Articles: {len(articles_by_id):,}", end='', flush=True)

            if 'INSERT INTO `blog_blogpost` VALUES' in line:
                in_article_insert = True
                buffer = [line]
            elif in_article_insert:
                buffer.append(line)
                # Continue jusqu'au point-virgule final
                if line.rstrip().endswith(';'):
                    full_insert = ''.join(buffer)
                    tuples = extract_insert_values(full_insert)

                    for values in tuples:
                        if len(values) >= 23:
                            try:
                                article_id = int(parse_sql_value(values[0]))

                                # Éviter les doublons (le dump contient la table 3 fois)
                                if article_id in articles_by_id:
                                    continue

                                title = parse_sql_value(values[6]) or ""
                                slug = parse_sql_value(values[7]) or ""
                                description = parse_sql_value(values[9]) or ""
                                content = parse_sql_value(values[18]) or ""
                                featured_image = parse_sql_value(values[20])
                                publish_date = parse_sql_value(values[14])
                                status = int(parse_sql_value(values[13]))
                                comments_count = int(parse_sql_value(values[1]))
                                user_id = int(parse_sql_value(values[22]))

                                # Lookup author
                                username, email = users.get(user_id, ("Unknown", ""))

                                article = Article(
                                    id=article_id,
                                    title=title,
                                    slug=slug,
                                    description=description,
                                    content=content,
                                    featured_image=featured_image,
                                    author_username=username,
                                    author_email=email,
                                    publish_date=publish_date,
                                    comments_count=comments_count,
                                    status=status
                                )

                                articles_by_id[article_id] = article
                                parsed_count += 1

                            except (ValueError, IndexError) as e:
                                # Skip malformed records silently
                                pass

                    in_article_insert = False
                    buffer = []

    articles = list(articles_by_id.values())
    print(f"\r   ✓ Found {len(articles)} unique articles ({parsed_count:,} records parsed)")
    return articles


def analyze_articles(articles: List[Article]) -> Statistics:
    """Analyse les articles et génère des statistiques"""
    stats = Statistics()

    for article in articles:
        stats.total_articles += 1

        if article.is_published:
            stats.published_articles += 1
        else:
            stats.draft_articles += 1

        if article.comments_count > 0:
            stats.articles_with_comments += 1

        if article.featured_image:
            stats.articles_with_images += 1
            if article.has_external_image:
                stats.articles_with_external_images += 1

        if article.title_too_long:
            stats.titles_too_long += 1
            stats.problem_articles.append(article)

        if article.description_too_long:
            stats.descriptions_too_long += 1
            if article not in stats.problem_articles:
                stats.problem_articles.append(article)

        stats.author_count[article.author_username] += 1
        stats.content_lengths.append(len(article.content))

    return stats


def generate_markdown_report(stats: Statistics, articles: List[Article]):
    """Génère le rapport au format Markdown"""
    report_path = Path(REPORT_FILE)
    report_path.parent.mkdir(parents=True, exist_ok=True)

    with open(report_path, 'w', encoding='utf-8') as f:
        f.write("# Rapport de Pré-Migration - Articles de Blog\n\n")
        f.write(f"**Date:** {datetime.now().isoformat()}\n")
        f.write("**Source:** `blog_blogpost` table (Django/MySQL)\n")
        f.write("**Destination:** `authoring_article` (Spring Boot/PostgreSQL)\n\n")
        f.write("---\n\n")

        # Vue d'ensemble
        f.write("## 📊 Vue d'ensemble\n\n")
        f.write(f"- **Total articles:** {stats.total_articles}\n")

        published_pct = 100.0 * stats.published_articles / stats.total_articles if stats.total_articles > 0 else 0
        f.write(f"- **Articles publiés:** {stats.published_articles} ({published_pct:.1f}%)\n")
        f.write(f"- **Brouillons:** {stats.draft_articles}\n")
        f.write(f"- **Articles avec commentaires:** {stats.articles_with_comments}\n")
        f.write(f"- **Articles avec images:** {stats.articles_with_images}\n")
        f.write(f"- **Images externes:** {stats.articles_with_external_images}\n\n")

        # Auteurs
        f.write("## ✍️ Auteurs\n\n")
        f.write(f"- **Nombre d'auteurs uniques:** {len(stats.author_count)}\n\n")
        f.write("| Auteur | Nombre d'articles |\n")
        f.write("|--------|-------------------|\n")

        top_authors = sorted(stats.author_count.items(), key=lambda x: x[1], reverse=True)[:10]
        for author, count in top_authors:
            f.write(f"| {author} | {count} |\n")
        f.write("\n")

        # Statistiques de contenu
        f.write("## 📝 Statistiques de contenu\n\n")
        if stats.content_lengths:
            avg_length = sum(stats.content_lengths) // len(stats.content_lengths)
            f.write(f"- **Longueur moyenne du contenu HTML:** {avg_length} caractères\n")
            f.write(f"- **Contenu le plus court:** {min(stats.content_lengths)} caractères\n")
            f.write(f"- **Contenu le plus long:** {max(stats.content_lengths)} caractères\n\n")

        # Problèmes identifiés
        f.write("## ⚠️ Problèmes identifiés\n\n")

        if stats.titles_too_long > 0:
            f.write(f"### Titres trop longs (> {MAX_TITLE_LENGTH} caractères)\n\n")
            f.write(f"**{stats.titles_too_long} articles concernés**\n\n")
            f.write("| ID | Titre (tronqué) | Longueur |\n")
            f.write("|----|-----------------|----------|\n")

            for article in [a for a in articles if a.title_too_long][:10]:
                truncated = article.title[:50] + "..." if len(article.title) > 50 else article.title
                f.write(f"| {article.id} | {truncated} | {len(article.title)} |\n")
            f.write("\n")

        if stats.descriptions_too_long > 0:
            f.write(f"### Descriptions trop longues (> {MAX_DESCRIPTION_LENGTH} caractères)\n\n")
            f.write(f"**{stats.descriptions_too_long} articles concernés**\n\n")
            f.write("| ID | Description (tronquée) | Longueur |\n")
            f.write("|----|------------------------|----------|\n")

            for article in [a for a in articles if a.description_too_long][:10]:
                truncated = article.description[:50] + "..." if len(article.description) > 50 else article.description
                # Nettoyer le HTML pour l'affichage
                truncated = re.sub(r'<[^>]+>', '', truncated)
                f.write(f"| {article.id} | {truncated} | {len(article.description)} |\n")
            f.write("\n")

        # Recommandations
        f.write("## 💡 Recommandations\n\n")

        if stats.descriptions_too_long > 0:
            f.write("1. **Descriptions:** Augmenter la limite de 200 à 500 caractères "
                   "dans le schéma de base de données\n")
        else:
            f.write("1. **Descriptions:** Limite actuelle (200 car) suffisante ✓\n")

        if stats.titles_too_long > 0:
            f.write(f"2. **Titres:** Tronquer les {stats.titles_too_long} "
                   "titres trop longs à 255 caractères\n")
        else:
            f.write("2. **Titres:** Limite actuelle (255 car) suffisante ✓\n")

        if stats.articles_with_external_images > 0:
            f.write(f"3. **Images externes:** Décision à prendre pour "
                   f"{stats.articles_with_external_images} images hébergées externement:\n")
            f.write("   - Option A: Garder les URLs (risque de liens cassés)\n")
            f.write("   - Option B: Télécharger et ré-héberger localement\n")

        f.write("\n---\n\n")
        f.write("**Prêt pour la migration:** ")

        if stats.titles_too_long == 0 and stats.descriptions_too_long < 10:
            f.write("✅ OUI\n")
        else:
            f.write("⚠️ Avec ajustements recommandés\n")


def generate_csv_report(articles: List[Article]):
    """Génère le rapport CSV détaillé"""
    csv_path = Path(CSV_ARTICLES_FILE)
    csv_path.parent.mkdir(parents=True, exist_ok=True)

    with open(csv_path, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)

        # Header
        writer.writerow([
            'id', 'title', 'slug', 'author', 'status', 'publish_date',
            'comments_count', 'title_length', 'description_length',
            'content_length', 'has_image', 'external_image', 'needs_truncation'
        ])

        for article in articles:
            writer.writerow([
                article.id,
                article.title,
                article.slug,
                article.author_username,
                'published' if article.is_published else 'draft',
                article.publish_date or '',
                article.comments_count,
                len(article.title),
                len(article.description),
                len(article.content),
                'Y' if article.featured_image else 'N',
                'Y' if article.has_external_image else 'N',
                'Y' if article.title_too_long or article.description_too_long else 'N'
            ])


def display_summary(stats: Statistics):
    """Affiche un résumé dans la console"""
    print()
    print("=" * 80)
    print("SUMMARY")
    print("=" * 80)
    print()
    print("📊 Articles:")
    print(f"   Total:     {stats.total_articles}")
    print(f"   Published: {stats.published_articles}")
    print(f"   Drafts:    {stats.draft_articles}")
    print()
    print(f"✍️  Authors:   {len(stats.author_count)}")
    print()
    print("⚠️  Issues:")
    print(f"   Titles too long:       {stats.titles_too_long}")
    print(f"   Descriptions too long: {stats.descriptions_too_long}")
    print(f"   External images:       {stats.articles_with_external_images}")
    print()
    print("=" * 80)
    print()

    if stats.titles_too_long == 0 and stats.descriptions_too_long == 0:
        print("✅ No critical issues found. Ready for migration!")
    else:
        print("⚠️  Some issues found. Review the report for details.")
    print()


def main():
    """Point d'entrée principal"""
    print("=" * 80)
    print("PRE-MIGRATION ANALYSIS - Blog Articles")
    print("=" * 80)
    print()

    dump_path = Path(DUMP_FILE)

    if not dump_path.exists():
        print(f"❌ Error: SQL dump file not found: {DUMP_FILE}")
        return 1

    print(f"📂 Found dump file: {DUMP_FILE}")
    print(f"   Size: {dump_path.stat().st_size / 1024 / 1024:.1f} MB")
    print()

    # Phase 1: Parse users
    print("📂 Phase 1: Parsing users...")
    users = parse_users(dump_path)
    print()

    # Phase 2: Parse articles
    print("📂 Phase 2: Parsing articles...")
    articles = parse_articles(dump_path, users)
    print()

    # Phase 3: Analyze
    print("📊 Phase 3: Analyzing articles...")
    stats = analyze_articles(articles)
    print("   ✓ Analysis complete")
    print()

    # Phase 4: Generate reports
    print("📄 Phase 4: Generating reports...")
    generate_markdown_report(stats, articles)
    generate_csv_report(articles)
    print(f"   ✓ Reports generated:")
    print(f"     - {REPORT_FILE}")
    print(f"     - {CSV_ARTICLES_FILE}")
    print()

    # Display summary
    display_summary(stats)

    return 0


if __name__ == '__main__':
    exit(main())
