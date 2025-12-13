package com.bloodbowlclub.migration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

/**
 * Script d'analyse préliminaire pour la migration des articles de blog
 * depuis l'ancien système Django/MySQL vers le nouveau système Authoring.
 *
 * Génère un rapport détaillé des données à migrer et identifie les problèmes potentiels.
 */
public class PreMigrationAnalysisScript {

    private static final String DUMP_FILE = "DataMigration/BBClub-2025_12_05_12_55_43-dump.sql";
    private static final String REPORT_FILE = "migration/pre-migration-report.md";
    private static final String CSV_ARTICLES_FILE = "migration/articles-analysis.csv";

    // Limites de la structure cible
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final int MAX_COMMENT_LENGTH = 1000;

    private static class ArticleData {
        int id;
        String title;
        String slug;
        String description;
        String content;
        String featuredImage;
        String authorUsername;
        String authorEmail;
        String publishDate;
        int commentsCount;
        int status;

        boolean titleTooLong() {
            return title != null && title.length() > MAX_TITLE_LENGTH;
        }

        boolean descriptionTooLong() {
            return description != null && description.length() > MAX_DESCRIPTION_LENGTH;
        }

        boolean hasExternalImage() {
            return featuredImage != null &&
                   (featuredImage.startsWith("http://") || featuredImage.startsWith("https://"));
        }
    }

    private static class Statistics {
        int totalArticles = 0;
        int publishedArticles = 0;
        int draftArticles = 0;
        int articlesWithComments = 0;
        int articlesWithImages = 0;
        int articlesWithExternalImages = 0;
        int titlesTooLong = 0;
        int descriptionsTooLong = 0;
        Map<String, Integer> authorCount = new HashMap<>();
        int avgContentLength = 0;
        int minContentLength = Integer.MAX_VALUE;
        int maxContentLength = 0;
        List<ArticleData> problemArticles = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("PRE-MIGRATION ANALYSIS - Blog Articles");
        System.out.println("=".repeat(80));
        System.out.println();

        try {
            // Phase 1: Parse SQL dump
            System.out.println("📂 Phase 1: Parsing SQL dump file...");
            List<ArticleData> articles = parseArticlesFromDump();
            System.out.println("   ✓ Found " + articles.size() + " articles");
            System.out.println();

            // Phase 2: Analyze statistics
            System.out.println("📊 Phase 2: Analyzing articles...");
            Statistics stats = analyzeArticles(articles);
            System.out.println("   ✓ Analysis complete");
            System.out.println();

            // Phase 3: Generate reports
            System.out.println("📄 Phase 3: Generating reports...");
            generateMarkdownReport(stats, articles);
            generateCsvReport(articles);
            System.out.println("   ✓ Reports generated:");
            System.out.println("     - " + REPORT_FILE);
            System.out.println("     - " + CSV_ARTICLES_FILE);
            System.out.println();

            // Display summary
            displaySummary(stats);

        } catch (Exception e) {
            System.err.println("❌ Error during analysis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Parse le dump SQL pour extraire les données des articles
     */
    private static List<ArticleData> parseArticlesFromDump() throws IOException {
        List<ArticleData> articles = new ArrayList<>();
        Path dumpPath = Paths.get(DUMP_FILE);

        if (!Files.exists(dumpPath)) {
            throw new FileNotFoundException("SQL dump file not found: " + DUMP_FILE);
        }

        System.out.println("   Reading file: " + DUMP_FILE);
        System.out.println("   File size: " + (Files.size(dumpPath) / 1024 / 1024) + " MB");

        // Patterns pour extraire les données
        Pattern insertPattern = Pattern.compile("INSERT INTO `blog_blogpost` VALUES");
        Pattern userPattern = Pattern.compile("INSERT INTO `auth_user` VALUES");

        // Map pour stocker les users (id -> username, email)
        Map<Integer, String[]> users = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(dumpPath, StandardCharsets.UTF_8)) {
            String line;
            boolean inBlogInsert = false;
            boolean inUserInsert = false;
            StringBuilder currentInsert = new StringBuilder();

            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;

                if (lineCount % 10000 == 0) {
                    System.out.print("\r   Processing line: " + lineCount);
                }

                // Détecter les insertions de users
                if (userPattern.matcher(line).find()) {
                    inUserInsert = true;
                    currentInsert = new StringBuilder(line);
                } else if (inUserInsert) {
                    currentInsert.append(line);
                    if (line.contains(";")) {
                        parseUsers(currentInsert.toString(), users);
                        inUserInsert = false;
                    }
                }

                // Détecter les insertions d'articles
                if (insertPattern.matcher(line).find()) {
                    inBlogInsert = true;
                    currentInsert = new StringBuilder(line);
                } else if (inBlogInsert) {
                    currentInsert.append(line);
                    if (line.contains(";")) {
                        parseArticles(currentInsert.toString(), users, articles);
                        inBlogInsert = false;
                    }
                }
            }

            System.out.println("\r   Processed " + lineCount + " lines");
        }

        return articles;
    }

    /**
     * Parse les données des utilisateurs depuis une ligne INSERT
     */
    private static void parseUsers(String insertStatement, Map<Integer, String[]> users) {
        // Pattern simplifié: (id,'username','first','last','email',...)
        Pattern valuePattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = valuePattern.matcher(insertStatement);

        while (matcher.find()) {
            String values = matcher.group(1);
            String[] parts = splitCsvLine(values);

            if (parts.length >= 5) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String username = unquote(parts[1]);
                    String email = unquote(parts[4]);
                    users.put(id, new String[]{username, email});
                } catch (Exception e) {
                    // Ignore malformed entries
                }
            }
        }
    }

    /**
     * Parse les données des articles depuis une ligne INSERT
     */
    private static void parseArticles(String insertStatement, Map<Integer, String[]> users,
                                     List<ArticleData> articles) {
        // Pattern pour extraire les valeurs entre parenthèses
        Pattern valuePattern = Pattern.compile("\\(([^)]+(?:\\([^)]*\\)[^)]*)*)\\)");
        Matcher matcher = valuePattern.matcher(insertStatement);

        while (matcher.find()) {
            String values = matcher.group(1);

            try {
                ArticleData article = new ArticleData();
                String[] parts = splitCsvLine(values);

                if (parts.length < 23) continue; // Vérifier qu'on a tous les champs

                article.id = Integer.parseInt(parts[0].trim());
                article.title = unquote(parts[6]);
                article.slug = unquote(parts[7]);
                article.description = unquote(parts[9]);
                article.content = unquote(parts[18]);
                article.featuredImage = unquote(parts[20]);
                article.publishDate = unquote(parts[14]);
                article.status = Integer.parseInt(parts[13].trim());
                article.commentsCount = Integer.parseInt(parts[1].trim());

                // Lookup author
                int userId = Integer.parseInt(parts[22].trim());
                String[] userInfo = users.get(userId);
                if (userInfo != null) {
                    article.authorUsername = userInfo[0];
                    article.authorEmail = userInfo[1];
                } else {
                    article.authorUsername = "Unknown";
                    article.authorEmail = "";
                }

                articles.add(article);

            } catch (Exception e) {
                System.err.println("\n   ⚠ Error parsing article: " + e.getMessage());
            }
        }
    }

    /**
     * Analyse les articles et génère des statistiques
     */
    private static Statistics analyzeArticles(List<ArticleData> articles) {
        Statistics stats = new Statistics();
        int totalContentLength = 0;

        for (ArticleData article : articles) {
            stats.totalArticles++;

            // Status
            if (article.status == 2) {
                stats.publishedArticles++;
            } else {
                stats.draftArticles++;
            }

            // Comments
            if (article.commentsCount > 0) {
                stats.articlesWithComments++;
            }

            // Images
            if (article.featuredImage != null && !article.featuredImage.isEmpty()) {
                stats.articlesWithImages++;
                if (article.hasExternalImage()) {
                    stats.articlesWithExternalImages++;
                }
            }

            // Length issues
            if (article.titleTooLong()) {
                stats.titlesTooLong++;
                stats.problemArticles.add(article);
            }
            if (article.descriptionTooLong()) {
                stats.descriptionsTooLong++;
                if (!stats.problemArticles.contains(article)) {
                    stats.problemArticles.add(article);
                }
            }

            // Authors
            stats.authorCount.merge(article.authorUsername, 1, Integer::sum);

            // Content length
            if (article.content != null) {
                int length = article.content.length();
                totalContentLength += length;
                stats.minContentLength = Math.min(stats.minContentLength, length);
                stats.maxContentLength = Math.max(stats.maxContentLength, length);
            }
        }

        stats.avgContentLength = stats.totalArticles > 0 ?
            totalContentLength / stats.totalArticles : 0;

        return stats;
    }

    /**
     * Génère le rapport au format Markdown
     */
    private static void generateMarkdownReport(Statistics stats, List<ArticleData> articles)
            throws IOException {
        Path reportPath = Paths.get(REPORT_FILE);
        Files.createDirectories(reportPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write("# Rapport de Pré-Migration - Articles de Blog\n\n");
            writer.write("**Date:** " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
            writer.write("**Source:** `blog_blogpost` table (Django/MySQL)\n");
            writer.write("**Destination:** `authoring_article` (Spring Boot/PostgreSQL)\n\n");

            writer.write("---\n\n");

            // Vue d'ensemble
            writer.write("## 📊 Vue d'ensemble\n\n");
            writer.write(String.format("- **Total articles:** %d\n", stats.totalArticles));
            writer.write(String.format("- **Articles publiés:** %d (%.1f%%)\n",
                stats.publishedArticles,
                stats.totalArticles > 0 ? 100.0 * stats.publishedArticles / stats.totalArticles : 0));
            writer.write(String.format("- **Brouillons:** %d\n", stats.draftArticles));
            writer.write(String.format("- **Articles avec commentaires:** %d\n", stats.articlesWithComments));
            writer.write(String.format("- **Articles avec images:** %d\n", stats.articlesWithImages));
            writer.write(String.format("- **Images externes:** %d\n\n", stats.articlesWithExternalImages));

            // Auteurs
            writer.write("## ✍️ Auteurs\n\n");
            writer.write(String.format("- **Nombre d'auteurs uniques:** %d\n\n", stats.authorCount.size()));
            writer.write("| Auteur | Nombre d'articles |\n");
            writer.write("|--------|-------------------|\n");

            stats.authorCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    try {
                        writer.write(String.format("| %s | %d |\n", entry.getKey(), entry.getValue()));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            writer.write("\n");

            // Statistiques de contenu
            writer.write("## 📝 Statistiques de contenu\n\n");
            writer.write(String.format("- **Longueur moyenne du contenu HTML:** %d caractères\n",
                stats.avgContentLength));
            writer.write(String.format("- **Contenu le plus court:** %d caractères\n",
                stats.minContentLength));
            writer.write(String.format("- **Contenu le plus long:** %d caractères\n\n",
                stats.maxContentLength));

            // Problèmes identifiés
            writer.write("## ⚠️ Problèmes identifiés\n\n");

            if (stats.titlesTooLong > 0) {
                writer.write(String.format("### Titres trop longs (> %d caractères)\n\n",
                    MAX_TITLE_LENGTH));
                writer.write(String.format("**%d articles concernés**\n\n", stats.titlesTooLong));

                writer.write("| ID | Titre (tronqué) | Longueur |\n");
                writer.write("|----|-----------------|----------|\n");

                articles.stream()
                    .filter(ArticleData::titleTooLong)
                    .limit(10)
                    .forEach(article -> {
                        try {
                            String truncated = article.title.substring(0,
                                Math.min(50, article.title.length())) + "...";
                            writer.write(String.format("| %d | %s | %d |\n",
                                article.id, truncated, article.title.length()));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                writer.write("\n");
            }

            if (stats.descriptionsTooLong > 0) {
                writer.write(String.format("### Descriptions trop longues (> %d caractères)\n\n",
                    MAX_DESCRIPTION_LENGTH));
                writer.write(String.format("**%d articles concernés**\n\n", stats.descriptionsTooLong));

                writer.write("| ID | Description (tronquée) | Longueur |\n");
                writer.write("|----|------------------------|----------|\n");

                articles.stream()
                    .filter(ArticleData::descriptionTooLong)
                    .limit(10)
                    .forEach(article -> {
                        try {
                            String truncated = article.description.substring(0,
                                Math.min(50, article.description.length())) + "...";
                            writer.write(String.format("| %d | %s | %d |\n",
                                article.id, truncated, article.description.length()));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                writer.write("\n");
            }

            // Recommandations
            writer.write("## 💡 Recommandations\n\n");

            if (stats.descriptionsTooLong > 0) {
                writer.write("1. **Descriptions:** Augmenter la limite de 200 à 500 caractères " +
                    "dans le schéma de base de données\n");
            } else {
                writer.write("1. **Descriptions:** Limite actuelle (200 car) suffisante ✓\n");
            }

            if (stats.titlesTooLong > 0) {
                writer.write("2. **Titres:** Tronquer les " + stats.titlesTooLong +
                    " titres trop longs à 255 caractères\n");
            } else {
                writer.write("2. **Titres:** Limite actuelle (255 car) suffisante ✓\n");
            }

            if (stats.articlesWithExternalImages > 0) {
                writer.write("3. **Images externes:** Décision à prendre pour " +
                    stats.articlesWithExternalImages + " images hébergées externement:\n");
                writer.write("   - Option A: Garder les URLs (risque de liens cassés)\n");
                writer.write("   - Option B: Télécharger et ré-héberger localement\n");
            }

            writer.write("\n---\n\n");
            writer.write("**Prêt pour la migration:** ");

            if (stats.titlesTooLong == 0 && stats.descriptionsTooLong < 10) {
                writer.write("✅ OUI\n");
            } else {
                writer.write("⚠️ Avec ajustements recommandés\n");
            }
        }
    }

    /**
     * Génère le rapport CSV détaillé
     */
    private static void generateCsvReport(List<ArticleData> articles) throws IOException {
        Path csvPath = Paths.get(CSV_ARTICLES_FILE);
        Files.createDirectories(csvPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            // Header
            writer.write("id,title,slug,author,status,publish_date,comments_count," +
                "title_length,description_length,content_length,has_image," +
                "external_image,needs_truncation\n");

            for (ArticleData article : articles) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,%d,%d,%d,%s,%s,%s\n",
                    article.id,
                    escapeCsv(article.title),
                    escapeCsv(article.slug),
                    escapeCsv(article.authorUsername),
                    article.status,
                    article.publishDate != null ? article.publishDate : "",
                    article.commentsCount,
                    article.title != null ? article.title.length() : 0,
                    article.description != null ? article.description.length() : 0,
                    article.content != null ? article.content.length() : 0,
                    article.featuredImage != null && !article.featuredImage.isEmpty() ? "Y" : "N",
                    article.hasExternalImage() ? "Y" : "N",
                    article.titleTooLong() || article.descriptionTooLong() ? "Y" : "N"
                ));
            }
        }
    }

    /**
     * Affiche un résumé dans la console
     */
    private static void displaySummary(Statistics stats) {
        System.out.println("=".repeat(80));
        System.out.println("SUMMARY");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("📊 Articles:");
        System.out.println("   Total:     " + stats.totalArticles);
        System.out.println("   Published: " + stats.publishedArticles);
        System.out.println("   Drafts:    " + stats.draftArticles);
        System.out.println();
        System.out.println("✍️  Authors:   " + stats.authorCount.size());
        System.out.println();
        System.out.println("⚠️  Issues:");
        System.out.println("   Titles too long:       " + stats.titlesTooLong);
        System.out.println("   Descriptions too long: " + stats.descriptionsTooLong);
        System.out.println("   External images:       " + stats.articlesWithExternalImages);
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println();

        if (stats.titlesTooLong == 0 && stats.descriptionsTooLong == 0) {
            System.out.println("✅ No critical issues found. Ready for migration!");
        } else {
            System.out.println("⚠️  Some issues found. Review the report for details.");
        }
        System.out.println();
    }

    // Utility methods

    private static String[] splitCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escaped) {
                current.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '\'' && !inQuotes) {
                inQuotes = true;
            } else if (c == '\'' && inQuotes) {
                inQuotes = false;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private static String unquote(String str) {
        if (str == null) return null;
        str = str.trim();
        if (str.startsWith("'") && str.endsWith("'")) {
            str = str.substring(1, str.length() - 1);
        }
        return str.replace("\\'", "'")
                  .replace("\\\"", "\"")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\\\", "\\");
    }

    private static String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }
}
