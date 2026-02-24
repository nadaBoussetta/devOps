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

-- Utilisateur de test (mot de passe: password123)
INSERT INTO users (username, email, password) VALUES
('testuser', 'test@example.com', '$2a$10$3FWzCj66pQGQzgh0pqzkP.E2Q8zWY.GoUZehVhphPDaPPE0.Wkml2');

INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_USER');

