# Rapport de Pré-Migration - Articles de Blog

**Date:** 2025-12-05T14:20:32.226320
**Source:** `blog_blogpost` table (Django/MySQL)
**Destination:** `authoring_article` (Spring Boot/PostgreSQL)

---

## 📊 Vue d'ensemble

- **Total articles:** 306
- **Articles publiés:** 306 (100.0%)
- **Brouillons:** 0
- **Articles avec commentaires:** 304
- **Articles avec images:** 306
- **Images externes:** 306

## ✍️ Auteurs

- **Nombre d'auteurs uniques:** 34

| Auteur | Nombre d'articles |
|--------|-------------------|
| Legalodec | 123 |
| longusbarbe | 25 |
| Tyra | 23 |
| Bagouze | 18 |
| Elpigeon | 14 |
| DavidSycod | 12 |
| DwarfKeeper | 11 |
| giom | 8 |
| Chatman | 8 |
| Kaoragh | 8 |

## 📝 Statistiques de contenu

- **Longueur moyenne du contenu HTML:** 11804 caractères
- **Contenu le plus court:** 1128 caractères
- **Contenu le plus long:** 752728 caractères

## ⚠️ Problèmes identifiés

### Descriptions trop longues (> 200 caractères)

**212 articles concernés**

| ID | Description (tronquée) | Longueur |
|----|------------------------|----------|
| 2 |   Bienvenue sur le nouveau site de gestion du Gran... | 505 |
| 4 |   ... avec option d'achat !Affranchi de son statut... | 460 |
| 6 |   Ou comment coller les miquettes à toute la LdC..... | 418 |
| 8 |   Qui pour détroner Dents de Sabre, triple vainque... | 270 |
| 9 |   Tu es nouveau dans le milieu de l’ovalie à point... | 720 |
| 11 |   Un nouvel élan.Longtemps connu chez les korrigan... | 255 |
| 14 |   Traditionnellement, la première journée de la Li... | 586 |
| 16 |   … et se fait justement punir pour sa peine !Alor... | 649 |
| 17 |   "Mais le haut-pouldu, c'est chez moi bordel !"C'... | 224 |
| 19 |   Qu’on se le dise : gobelins et nouvelles technol... | 412 |

## 💡 Recommandations

1. **Descriptions:** Augmenter la limite de 200 à 500 caractères dans le schéma de base de données
2. **Titres:** Limite actuelle (255 car) suffisante ✓
3. **Images externes:** Décision à prendre pour 306 images hébergées externement:
   - Option A: Garder les URLs (risque de liens cassés)
   - Option B: Télécharger et ré-héberger localement

---

**Prêt pour la migration:** ⚠️ Avec ajustements recommandés
