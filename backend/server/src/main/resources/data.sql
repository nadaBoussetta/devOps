-- Insertion de données de test pour les bibliothèques

-- Bibliothèques universitaires
INSERT INTO bibliotheques (nom, adresse, latitude, longitude, type, note_globale, nombre_notations) VALUES
('Bibliothèque Sainte-Geneviève', '10 Place du Panthéon, 75005 Paris', 48.8462, 2.3464, 'UNIVERSITAIRE', 4.5, 120),
('Bibliothèque Sorbonne', '17 Rue de la Sorbonne, 75005 Paris', 48.8489, 2.3436, 'UNIVERSITAIRE', 4.3, 95),
('Bibliothèque Paris Descartes', '12 Rue de l''École de Médecine, 75006 Paris', 48.8506, 2.3420, 'UNIVERSITAIRE', 4.0, 78),
('BU Sciences Po', '30 Rue Saint-Guillaume, 75007 Paris', 48.8545, 2.3265, 'UNIVERSITAIRE', 4.7, 150);

-- Bibliothèques manquantes (pour matcher les IDs utilisés dans les horaires)
INSERT INTO bibliotheques (nom, adresse, latitude, longitude, type, note_globale, nombre_notations) VALUES
('Bibliothèque François Mitterrand', 'Quai François Mauriac, 75013 Paris', 48.8352, 2.3768, 'PUBLIQUE', 4.6, 200),
('Bibliothèque Richelieu', '58 Rue de Richelieu, 75002 Paris', 48.8685, 2.3380, 'PUBLIQUE', 4.4, 110),
('Bibliothèque Forney', '1 Rue du Figuier, 75004 Paris', 48.8520, 2.3570, 'PUBLIQUE', 4.2, 65),
('Bibliothèque Marguerite Duras', '79 Rue de Charenton, 75012 Paris', 48.8490, 2.3800, 'PUBLIQUE', 4.1, 45);

-- Nouvelles bibliothèques (9 à 12)
INSERT INTO bibliotheques (nom, adresse, latitude, longitude, type, note_globale, nombre_notations) VALUES
('Bibliothèque Georges Brassens', '38 Rue Gassendi, 75014 Paris', 48.8335, 2.3238, 'PUBLIQUE', 4.2, 52),
('Bibliothèque Léo Ferré', '2 Rue des Maraîchers, 75020 Paris', 48.8517, 2.4052, 'PUBLIQUE', 4.0, 31),
('Médiathèque Marguerite Yourcenar', '41 Rue d''Alleray, 75015 Paris', 48.8399, 2.3041, 'PUBLIQUE', 4.6, 98),
('BU Jussieu - Sciences', '4 Place Jussieu, 75005 Paris', 48.8463, 2.3551, 'UNIVERSITAIRE', 4.4, 130);

-- Bibliothèques supplémentaires (13 à 18)
INSERT INTO bibliotheques (nom, adresse, latitude, longitude, type, note_globale, nombre_notations) VALUES
('Bibliothèque Buffon', '15 Bis Rue Buffon, 75005 Paris', 48.8431, 2.3612, 'PUBLIQUE', 4.1, 60),
('Bibliothèque André Malraux', '112 Rue de Rennes, 75006 Paris', 48.8469, 2.3294, 'PUBLIQUE', 4.0, 48),
('Bibliothèque Aimé Césaire', '5 Rue de Ridder, 75014 Paris', 48.8308, 2.3207, 'PUBLIQUE', 4.3, 72),
('Bibliothèque Chaptal', '26 Rue Chaptal, 75009 Paris', 48.8782, 2.3348, 'PUBLIQUE', 3.9, 35),
('BU Paris 1 Panthéon', '12 Place du Panthéon, 75005 Paris', 48.8465, 2.3455, 'UNIVERSITAIRE', 4.4, 90),
('BU Paris 8 Saint-Denis (antenne Paris)', '2 Rue de la Liberté, 93200 Saint-Denis', 48.9352, 2.3574, 'UNIVERSITAIRE', 4.2, 55);

---------------------------------------------------------
-- HORAIRES
---------------------------------------------------------

-- Horaires pour Bibliothèque Sainte-Geneviève (id=1)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(1, 'LUNDI', '10:00:00', '22:00:00'),
(1, 'MARDI', '10:00:00', '22:00:00'),
(1, 'MERCREDI', '10:00:00', '22:00:00'),
(1, 'JEUDI', '10:00:00', '22:00:00'),
(1, 'VENDREDI', '10:00:00', '22:00:00'),
(1, 'SAMEDI', '10:00:00', '19:00:00');

-- Horaires pour Bibliothèque Sorbonne (id=2)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(2, 'LUNDI', '09:00:00', '20:00:00'),
(2, 'MARDI', '09:00:00', '20:00:00'),
(2, 'MERCREDI', '09:00:00', '20:00:00'),
(2, 'JEUDI', '09:00:00', '20:00:00'),
(2, 'VENDREDI', '09:00:00', '20:00:00'),
(2, 'SAMEDI', '09:00:00', '18:00:00');

-- Horaires pour Bibliothèque Paris Descartes (id=3)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(3, 'LUNDI', '08:30:00', '19:00:00'),
(3, 'MARDI', '08:30:00', '19:00:00'),
(3, 'MERCREDI', '08:30:00', '19:00:00'),
(3, 'JEUDI', '08:30:00', '19:00:00'),
(3, 'VENDREDI', '08:30:00', '19:00:00');

-- Horaires pour BU Sciences Po (id=4)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(4, 'LUNDI', '08:00:00', '23:00:00'),
(4, 'MARDI', '08:00:00', '23:00:00'),
(4, 'MERCREDI', '08:00:00', '23:00:00'),
(4, 'JEUDI', '08:00:00', '23:00:00'),
(4, 'VENDREDI', '08:00:00', '23:00:00'),
(4, 'SAMEDI', '09:00:00', '20:00:00'),
(4, 'DIMANCHE', '10:00:00', '20:00:00');

-- Horaires pour Bibliothèque François Mitterrand (id=5)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(5, 'LUNDI', '09:00:00', '20:00:00'),
(5, 'MARDI', '09:00:00', '20:00:00'),
(5, 'MERCREDI', '09:00:00', '20:00:00'),
(5, 'JEUDI', '09:00:00', '20:00:00'),
(5, 'VENDREDI', '09:00:00', '20:00:00'),
(5, 'SAMEDI', '09:00:00', '19:00:00'),
(5, 'DIMANCHE', '13:00:00', '19:00:00');

-- Horaires pour Bibliothèque Richelieu (id=6)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(6, 'LUNDI', '10:00:00', '18:00:00'),
(6, 'MARDI', '10:00:00', '18:00:00'),
(6, 'MERCREDI', '10:00:00', '18:00:00'),
(6, 'JEUDI', '10:00:00', '18:00:00'),
(6, 'VENDREDI', '10:00:00', '18:00:00');

-- Horaires pour Bibliothèque Forney (id=7)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(7, 'MARDI', '13:00:00', '19:00:00'),
(7, 'MERCREDI', '10:00:00', '19:00:00'),
(7, 'JEUDI', '10:00:00', '19:00:00'),
(7, 'VENDREDI', '10:00:00', '19:00:00'),
(7, 'SAMEDI', '10:00:00', '19:00:00');

-- Horaires pour Bibliothèque Marguerite Duras (id=8)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(8, 'MARDI', '14:00:00', '19:00:00'),
(8, 'MERCREDI', '10:00:00', '19:00:00'),
(8, 'VENDREDI', '14:00:00', '19:00:00'),
(8, 'SAMEDI', '10:00:00', '18:00:00');

-- Horaires pour Bibliothèque Georges Brassens (id=9)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(9, 'MARDI', '13:00:00', '19:00:00'),
(9, 'MERCREDI', '10:00:00', '19:00:00'),
(9, 'JEUDI', '13:00:00', '19:00:00'),
(9, 'VENDREDI', '13:00:00', '19:00:00'),
(9, 'SAMEDI', '10:00:00', '17:00:00');

-- Horaires pour Bibliothèque Léo Ferré (id=10)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(10, 'MARDI', '13:00:00', '19:00:00'),
(10, 'MERCREDI', '10:00:00', '19:00:00'),
(10, 'JEUDI', '13:00:00', '19:00:00'),
(10, 'VENDREDI', '13:00:00', '19:00:00'),
(10, 'SAMEDI', '10:00:00', '18:00:00');

-- Horaires pour Médiathèque Marguerite Yourcenar (id=11)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(11, 'MARDI', '13:00:00', '19:00:00'),
(11, 'MERCREDI', '10:00:00', '19:00:00'),
(11, 'JEUDI', '13:00:00', '19:00:00'),
(11, 'VENDREDI', '13:00:00', '19:00:00'),
(11, 'SAMEDI', '10:00:00', '18:00:00'),
(11, 'DIMANCHE', '13:00:00', '18:00:00');

-- Horaires pour BU Jussieu - Sciences (id=12)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(12, 'LUNDI', '08:30:00', '21:00:00'),
(12, 'MARDI', '08:30:00', '21:00:00'),
(12, 'MERCREDI', '08:30:00', '21:00:00'),
(12, 'JEUDI', '08:30:00', '21:00:00'),
(12, 'VENDREDI', '08:30:00', '21:00:00'),
(12, 'SAMEDI', '09:00:00', '19:00:00');

-- Horaires pour Bibliothèque Buffon (id=13)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(13, 'MARDI', '10:00:00', '19:00:00'),
(13, 'MERCREDI', '10:00:00', '19:00:00'),
(13, 'JEUDI', '10:00:00', '19:00:00'),
(13, 'VENDREDI', '10:00:00', '19:00:00'),
(13, 'SAMEDI', '10:00:00', '18:00:00');

-- Horaires pour Bibliothèque André Malraux (id=14)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(14, 'LUNDI', '13:00:00', '19:00:00'),
(14, 'MARDI', '10:00:00', '19:00:00'),
(14, 'MERCREDI', '10:00:00', '19:00:00'),
(14, 'JEUDI', '10:00:00', '19:00:00'),
(14, 'VENDREDI', '10:00:00', '19:00:00');

-- Horaires pour Bibliothèque Aimé Césaire (id=15)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(15, 'MARDI', '13:00:00', '19:00:00'),
(15, 'MERCREDI', '10:00:00', '19:00:00'),
(15, 'JEUDI', '13:00:00', '19:00:00'),
(15, 'VENDREDI', '13:00:00', '19:00:00'),
(15, 'SAMEDI', '10:00:00', '17:00:00');

-- Horaires pour Bibliothèque Chaptal (id=16)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(16, 'MARDI', '12:00:00', '18:00:00'),
(16, 'MERCREDI', '10:00:00', '18:00:00'),
(16, 'JEUDI', '12:00:00', '18:00:00'),
(16, 'VENDREDI', '12:00:00', '18:00:00');

-- Horaires pour BU Paris 1 Panthéon (id=17)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(17, 'LUNDI', '08:30:00', '20:00:00'),
(17, 'MARDI', '08:30:00', '20:00:00'),
(17, 'MERCREDI', '08:30:00', '20:00:00'),
(17, 'JEUDI', '08:30:00', '20:00:00'),
(17, 'VENDREDI', '08:30:00', '19:00:00');

-- Horaires pour BU Paris 8 (id=18)
INSERT INTO horaires (bibliotheque_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
(18, 'LUNDI', '09:00:00', '18:00:00'),
(18, 'MARDI', '09:00:00', '18:00:00'),
(18, 'MERCREDI', '09:00:00', '18:00:00'),
(18, 'JEUDI', '09:00:00', '18:00:00'),
(18, 'VENDREDI', '09:00:00', '17:00:00');

---------------------------------------------------------
-- UTILISATEURS
---------------------------------------------------------

-- Utilisateurs (mot de passe: password123 pour tous)
INSERT INTO users (username, email, password) VALUES
('testuser', 'test@example.com', '$2a$10$3FWzCj66pQGQzgh0pqzkP.E2Q8zWY.GoUZehVhphPDaPPE0.Wkml2'),
('alice',    'alice@example.com', '$2a$10$3FWzCj66pQGQzgh0pqzkP.E2Q8zWY.GoUZehVhphPDaPPE0.Wkml2'),
('bob',      'bob@example.com',   '$2a$10$3FWzCj66pQGQzgh0pqzkP.E2Q8zWY.GoUZehVhphPDaPPE0.Wkml2'),
('admin',    'admin@example.com', '$2a$10$3FWzCj66pQGQzgh0pqzkP.E2Q8zWY.GoUZehVhphPDaPPE0.Wkml2');

-- Rôles des utilisateurs
-- On suppose : testuser=id 1, alice=id 2, bob=id 3, admin=id 4
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_ADMIN');