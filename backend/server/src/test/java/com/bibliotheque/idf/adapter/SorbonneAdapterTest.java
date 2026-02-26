package com.bibliotheque.idf.adapter;

import devOps.adapter.SorbonneAdapter;
import devOps.dtos.LivreResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SorbonneAdapterTest {


    @Test
    void rechercherLivre_shouldReturnEmptyListWhenNoMatch() {
        SorbonneAdapter adapter = new SorbonneAdapter();

        List<LivreResponseDTO> resultats = adapter.rechercherLivre("titre introuvable");

        assertNotNull(resultats);
        assertTrue(resultats.isEmpty());
    }

}