-- ====================================
-- Lieux
-- ====================================
INSERT INTO lieu_entity (id, nom, type, adresse, coordonnees, niv_calme, wifi) VALUES ('L1', 'Bibliothèque Nationale', 'BIBLIOTHEQUE', '1 Rue de Richelieu, Paris', '48.8566,2.3522', 4, true);

INSERT INTO lieu_entity (id, nom, type, adresse, coordonnees, niv_calme, wifi) VALUES ('L2', 'Médiathèque Centre-Ville', 'MEDIATHEQUE', '10 Avenue des Champs, Paris', '48.8600,2.3600', 2, false);

-- ====================================
-- Livres
-- ====================================
INSERT INTO livre_entity (id, titre, auteur, isbn) VALUES ('B1', 'Harry', 'J. K. Rowling', '978-2070584621');

INSERT INTO livre_entity (id, titre, auteur, isbn) VALUES ('B2', 'Le Seigneur des Anneaux', 'J. R. R. Tolkien', '978-2266120989');

INSERT INTO livre_entity (id, titre, auteur, isbn) VALUES ('B3', '1984', 'George Orwell', '978-2070368228');

-- ====================================
-- Disponibilités
-- ====================================
INSERT INTO disponibilite_entity (id, bibliotheque, livre_dispo, lieu_id, livre_id) VALUES ('D1', 'Bibliothèque Nationale', true, 'L1', 'B1');

INSERT INTO disponibilite_entity (id, bibliotheque, livre_dispo, lieu_id, livre_id) VALUES ('D2', 'Bibliothèque Nationale', true, 'L1', 'B2');

INSERT INTO disponibilite_entity (id, bibliotheque, livre_dispo, lieu_id, livre_id) VALUES ('D3', 'Bibliothèque Nationale', false, 'L1', 'B3');

INSERT INTO disponibilite_entity (id, bibliotheque, livre_dispo, lieu_id, livre_id) VALUES ('D4', 'Médiathèque Centre-Ville', true, 'L2', 'B1');