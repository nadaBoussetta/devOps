[![Tests OK](https://github.com/nadaBoussetta/devOps/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/nadaBoussetta/devOps/actions/workflows/main.yml)

[![Sonar & Coverage](https://github.com/nadaBoussetta/devOps/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/nadaBoussetta/devOps/actions/workflows/ci.yml)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=nadaBoussetta_devOps&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps)  [![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=nadaBoussetta_devOps&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps)  [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=nadaBoussetta_devOps&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nadaBoussetta_devOps&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps)

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=nadaBoussetta_devOps)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps)  [![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=nadaBoussetta_devOps)



## Sommaire
- [Qui sommes-nous ?](#qui-sommes-nous-)
- [À propos du projet](#à-propos-du-projet)
- [Public visé](#public-visé)
- [Concurrence](#concurrence)
- [Outils et Processus DevOps](#outils-et-processus-devops)
- [Comment lancer ?](#comment-lancer-)
- [Équipe](#équipe)


## Qui sommes-nous ?
Nous sommes une équipe de quatre étudiants en Master 1 MIAGE, à l’Université Paris Nanterre.

## À propos du projet

L’application centralise les bibliothèques d’Île-de-France pour :

 - Rechercher les bibliothèques ouvertes à proximité

- Vérifier la disponibilité d’un livre spécifique

- Créer des sessions de révision avec timer

- Partager des publications et commentaires

- Recevoir des notifications sur les événements importants

## Public visé

- Étudiants : pour trouver des bibliothèques et livres rapidement et planifier leurs études.

- Chercheurs : pour localiser les ressources exactes dans les bibliothèques universitaires.

- Bibliophiles et utilisateurs d’espaces calmes : pour découvrir de nouvelles bibliothèques et gérer leur temps.

## Concurrence

| Solution                  | Type              | Avantages                  | Limites                                 | Notre approche                             |
|---------------------------|-----------------|----------------------------|----------------------------------------|-------------------------------------------|
| Google Maps               | Géolocalisation  | Gratuit, universel         | Pas d’info sur les livres, pas social  | Centralisation + recherche livres + feed  |
| WorldCat / OpenLibrary    | Catalogue livres | Large catalogue            | Pas de géolocalisation ni horaires     | Ajout géolocalisation et horaires         |
| Muggerino                 | Study spots      | Infos sur affluence        | Moins centré livres, peu social        | Infos livres + interaction utilisateur    |
| Sites web locaux           | Info bibliothèque| Horaires et collections    | Sites indépendants                      | Comparatif centralisé                      |

## Outils et Processus DevOps

Pour garantir la qualité et la maintenabilité du code, nous avons mis en place une chaîne DevOps complète :

### Gestion de version

- **Git & GitHub** : Utilisation ds branches et commande git pour la gestion et résolution des conflits

### Qualité du code (QA)

- **SonarCloud** : Analyse statique du code, détection de bugs, "code smells" et suivi de la couverture.
- **JaCoCo** : Génération des rapports de couverture de tests Java.

### Tests

- **JUnit 5** : Tests unitaires et d’intégration pour valider la logique métier et éviter les régressions.

### Comment lancer ?
Avant de lancer l'application, assurez-vous d'être à la **racine du projet** dans votre terminal.

Pour démarrer l'application complète (**backend** et **frontend**) :

1. Ouvrez un terminal à la racine du projet.
2. Exécutez la commande suivante :
   ```bash
   cmd /c launch.bat
3.Le script launch.bat va lancer le backend dans un premier terminal et lancer le frontend dans un second terminal
4.Une fois les deux services démarrés, vous pourrez accéder à l'application via votre navigateur à l'adresse suivante : **http://localhost:5500/index.html**

## Équipe

| Membre                       | GitHub           |
|-------------------------------|-----------------|
| KACI Lilia                    | @liliazkc      |
| BOUSSETTA Nada                | @nadaBoussetta  |
| ELHADJ MIMOUNE Nour El Islem  | @nourelislm93700  |
| AFOUCHAL Assia                | @Assiaafouchal  |
