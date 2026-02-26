package com.bibliotheque.idf.models;

import devOps.models.HoraireEntity;
import devOps.models.LibraryEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryEntityTest {

    @Test
    void addHoraire_shouldLinkHoraireToLibrary() {
        LibraryEntity library = new LibraryEntity();
        HoraireEntity horaire = new HoraireEntity();

        library.addHoraire(horaire);

        assertEquals(1, library.getHoraires().size());
        assertTrue(library.getHoraires().contains(horaire));
        assertEquals(library, horaire.getBibliotheque());
    }

    @Test
    void updateNoteGlobale_shouldRecomputeAverage() {
        LibraryEntity library = new LibraryEntity();
        library.setNoteGlobale(4.0);
        library.setNombreNotations(1);

        library.updateNoteGlobale(2);

        assertEquals(2, library.getNombreNotations());
        assertEquals(3.0, library.getNoteGlobale());
    }
}