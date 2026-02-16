-- =====================================================
--  BASE DE DONNÉES
-- =====================================================

CREATE DATABASE IF NOT EXISTS ma_biblio_DB
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ma_biblio_DB ;

-- =====================================================
--  ENUM TYPE_LIEU (lookup table)
-- =====================================================

CREATE TABLE type_lieu (
    code VARCHAR(50) PRIMARY KEY
);

INSERT INTO type_lieu (code) VALUES
('BIBLIOTHEQUE_UNIVERSITAIRE'),
('BIBLIOTHEQUE'),
('MEDIATHEQUE'),
('COWORKING_SPACE'),
('COWORKING_COFFEE');

-- =====================================================
--  UTILISATEUR
-- =====================================================

CREATE TABLE utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    mail VARCHAR(255),
    localisation VARCHAR(255)
);

-- =====================================================
--  LIVRE
-- =====================================================

CREATE TABLE livre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    categorie VARCHAR(255),
    description TEXT,
    image_url VARCHAR(512)
);

-- =====================================================
--  LIBRARY (Bibliothèque)
-- =====================================================

CREATE TABLE library (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    heures_ouverture VARCHAR(2000)
);

-- =====================================================
--  LIEU
-- =====================================================

CREATE TABLE lieu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    type VARCHAR(50),
    adresse VARCHAR(255),
    coordonnees VARCHAR(255),
    niv_calme INT,
    wifi BOOLEAN,
    library_id VARCHAR(255) UNIQUE,

    CONSTRAINT fk_lieu_type
        FOREIGN KEY (type)
        REFERENCES type_lieu(code),

    CONSTRAINT fk_lieu_library
        FOREIGN KEY (library_id)
        REFERENCES library(id)
        ON DELETE SET NULL
);

-- =====================================================
--  PUBLICATION
-- =====================================================

CREATE TABLE publication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    date TIMESTAMP,
    utilisateur_id BIGINT NOT NULL,
    repondeur_id BIGINT,

    CONSTRAINT fk_publication_utilisateur
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_publication_repondeur
        FOREIGN KEY (repondeur_id)
        REFERENCES utilisateur(id)
        ON DELETE SET NULL
);

-- =====================================================
--  AVIS
-- =====================================================

CREATE TABLE avis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note INT,
    description TEXT,
    utilisateur_id BIGINT,
    lieu_id BIGINT,

    CONSTRAINT fk_avis_utilisateur
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_avis_lieu
        FOREIGN KEY (lieu_id)
        REFERENCES lieu(id)
        ON DELETE CASCADE
);

-- =====================================================
--  DISPONIBILITE
-- =====================================================

CREATE TABLE disponibilite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bibliotheque VARCHAR(255),
    livre_dispo BOOLEAN,
    lieu_id BIGINT,
    livre_id BIGINT UNIQUE,

    CONSTRAINT fk_disponibilite_lieu
        FOREIGN KEY (lieu_id)
        REFERENCES lieu(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disponibilite_livre
        FOREIGN KEY (livre_id)
        REFERENCES livre(id)
        ON DELETE CASCADE
);

-- =====================================================
--  FAVORI
-- =====================================================

CREATE TABLE favori (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    livre_id BIGINT NOT NULL,

    CONSTRAINT fk_favori_utilisateur
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favori_livre
        FOREIGN KEY (livre_id)
        REFERENCES livre(id)
        ON DELETE CASCADE,

    UNIQUE (utilisateur_id, livre_id)
);

-- =====================================================
--  RECHERCHE
-- =====================================================

CREATE TABLE recherche (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    depart VARCHAR(255),
    coordonnees VARCHAR(255),
    utilisateur_id BIGINT,

    CONSTRAINT fk_recherche_utilisateur
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur(id)
        ON DELETE CASCADE
);

-- =====================================================
--  FIN
-- =====================================================
