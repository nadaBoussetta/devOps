package com.bibliotheque.idf.adapter;

import devOps.adapter.ParisDescarteAdapter;
import devOps.dtos.LivreResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParisDescarteAdapterTest {

    @Test
    void rechercherLivre_shouldReturnEmptyListWhenNoMatch() {
        ParisDescarteAdapter adapter = new ParisDescarteAdapter();

        List<LivreResponseDTO> resultats =
                adapter.rechercherLivre("titre introuvable");

        assertNotNull(resultats);
        assertTrue(resultats.isEmpty());
    }
}