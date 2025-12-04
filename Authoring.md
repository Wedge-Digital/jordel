
# Description du besoin liés au fonction d'authoring : 

## Besoin métier: 
L'authoring désigne la production de contenu pour les applications mobiles et web. En l'occurrence,
l'application bloodbowlClub, contient une partie, ou Domaine, relatif à la création de contenu, type article de blog. 

Chaque article peut être crée, modifié, ou supprimé. 
Chaque article est caractérisé par : 
- un identifiant unique de type ULID
- un auteur: le nom de coach de l'utilisateur qui a réalisé l'article
- une photo de profil de l'utilisateur qui a écrit l'article
- un titre: un text de 255 caractères maximum
- une date de création
- une description : un texte de 200 caractères qui décrit le contenu de l'article
- une image : une url cloudinary
- un paragraphe de tête: texte de 10000 caractères maximum
- un ou plusieurs sous-paragraphe composés eux mêmes : 
  - d'un titre 
  - d'un contenu texte, de 10000 caractères maximum

Chaque article peut être commenté par un utilisateur connecté. 
Un commentaire est composé : 
- d'un ID de type ULID
- d'un autheur : le nom de coach de l'utilisateur qui a réalisé le commentaire
- d'une photo de profil de l'utilisateur qui a écrit le commentaire
- d'une date de création 
- d'un contenu de commentaire, un texte de 1000 caractères maximum
- de l'ID de l'article auquel il se réfère

Un commentaire ne peut être que supprimé, ou ajouté

Le besoin est vraiment celui d'un blog, ultra classique. 
Les contenus doit pouvoir contenir du texte au format markdown,
ainsi que des emojis

## Architecture Technique

Le domaine fonctionnel de l'authoring est plus simple que celui de l'authentification, par exemple. 
En conséquence, il n'est pas besoin d'appliquer une architecture à base de CQRS / Event sourcing pour ce domaine. 

Tous le code lié à l'authoring doit être dans le domaine Authoring. Le code est situé dans le dossier Authoring, dans 
src/main/java/com/bloodbowlclub/authoring. 
Il n'y aura pas de service de partage entre les domaines pour le moment.

Un simple CRUD direct suffira. En conséquence, nous pouvons avoir uniquement deux couches dans le domaine Authoring :
- le Web Controller : receptionne les requêtes Rest
- le Service : traite les requêtes et interagit avec la base de données directement
- le repository : pour accèder à la table Authoring__Article, via l'entité ArticleEntity, ainsi qu'a la table Authoring_comments via l'entité correspondante. 
- La table comment référence la table Article via une clef étrangère.


### Requêtes du web Controller : 
### endpoint 1: 
-Liste des articles: 
     - Verbe HTTP : GET
     - URL : /authoring/article?page=<page> 
     - objectif : lister les articles présents
     - triés par ordre chronologique inverse, 
     - paginés 20 par 20, en partant par le numéro de page
     - la structure de réponse contient une liste de synthèse d'articles, un nombre d'article, et la pagee courante
     - la structure de synthése d'article contient, pour chaque article : 
            - l'id 
            - le titre 
            - l'auteur et sa photo 
            - la description de l'article 
            - l'url de l'image de présentation 

### endpoint 2:
- Contenu d'un article : 
  - Verbe HTTP : GET
  - URL : /authoring/article/<id>
  - Objectif: renvoyer le détail d'un article 
  - le detail d'un article contient toutes les informations de l'article plus la liste de tous les commentaires se rapportant a cet article. 

### endpoint 3:
- Création d'un Article : 
  - Verbe HTTP: POST
  - Url: /authoring/article
  - contenu de la requête : 
    - id de l'article 
    - titre
    - image 
    - auteur et photo de l'auteur 
    - contenu
    - paragraphes optionnels avec titre et contenus
  - la date de création est enregistrée automatiquement lors de la création 

### endpoint 4:
- Modification d'un article : 
  - Verbe HTTP: PUT
  - même fonctionnement que le POST

### endpoint 5:
- création d'un commentaire : 
  - Verbe Http POST
  - Url : /authoring/article/<id>/comment
  - contenu : 
    - id du commentaire
    - contenu du commentaire
    - auteur et photo de l'auteur
    - date de création automatique