# Document Technique — Projet DevOps Bibliothèques IDF

## Sommaire
- [Présentation du projet](#présentation-du-projet)
- [Architecture technique](#architecture-technique)
- [Technologies utilisées](#technologies-utilisées)
- [Lancement du projet](#lancement-du-projet)
- [Tests et qualité logicielle](#tests-et-qualité-logicielle)


## Présentation du projet
Ce projet est une application web permettant de rechercher des bibliothèques en Île-de-France. L'utilisateur peut rechercher des bibliothèques par adresse, horaires et rayon de distance, noter les bibliothèques, gérer ses favoris, et interagir via un feed social avec publications et commentaires.
Le backend est développé en Java 17 avec Spring Boot 3.2.0. Le frontend est une application JavaScript qui communique avec le backend via une API REST.

## Architecture technique
Le projet adopte une architecture Maven multi-module composée de trois niveaux :

- backend/ — POM parent (devOps:backend) qui hérite de spring-boot-starter-parent 3.2.0 et orchestre les deux sous-modules.
- backend/server/ — Module principal avec la logique métier (services, repositories, sécurité, configuration Spring).
- backend/rest-api/ 

## Technologies utilisées

| Couche                  | Technologie              | 
|---------------------------|-----------------|
| Langage             | Java  | 
| Framework  | Spring Boot 3.2.0 | 
| Persistance                 | Spring Data JPA    | 
| Build          | Maven | 
| Tests                 | JUnit 5 + Mockito  | 
| Couverture          | JaCoCo 0.8.11 | 
| Qualité          | SonarCloud | 
| CI/CD          | GitHub Actions | 

## Lancement du projet
Avant de lancer l'application, assurez-vous d'être à la **racine du projet** dans votre terminal.

Pour démarrer l'application complète (**backend** et **frontend**) :

1. Ouvrez un terminal à la racine du projet.
2. Exécutez la commande suivante :
   ```bash
   cmd /c launch.bat
3.Le script launch.bat va lancer le backend dans un premier terminal et lancer le frontend dans un second terminal. <br>4.Une fois les deux services démarrés, vous pourrez accéder à l'application via votre navigateur à l'adresse suivante : **http://localhost:5500/index.html**

## Tests et qualité logicielle

Les tests unitaires utilisent JUnit 5 et Mockito. Chaque service critique est testé de manière isolée avec des mocks sur les repositories.

Exemple — HoraireEntityTest vérifie que la méthode estOuvertSur() retourne true si la plage horaire demandée est incluse dans les horaires d'ouverture, et false sinon.

Exemple — FeedServiceTest vérifie la création de publications avec bibliothèque associée, l'ajout de commentaires, et le filtrage des commentaires de premier niveau uniquement.

Pour lancer les tests :

```bash
cd backend
mvn clean test
```

Le rapport JaCoCo est généré dans backend/server/target/site/jacoco/index.html après u

```bash
mvn clean verify
```

## Tests et qualité logicielle

Deux workflows GitHub Actions sont configurés sur la branche master :

- Tests OK (main.yml) — Déclenché à chaque push, il compile le projet et exécute tous les tests unitaires avec
```bash
mvn clean test
```
depuis backend/.

- Sonar & Coverage (ci.yml) — Déclenché à chaque push, il exécute mvn clean verify pour générer le rapport JaCoCo, puis envoie l'analyse à SonarCloud pour mesurer la qualité du code (maintenabilité, fiabilité, sécurité, couverture).
Les badges de statut sont disponibles dans le README du dépôt GitHub.
