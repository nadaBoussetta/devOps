package com.bibliotheque.idf.models;

import devOps.models.HoraireEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class HoraireEntityTest {

    @Test
    void estOuvertSur_shouldReturnTrueWhenInsideRange() {
        HoraireEntity horaire = new HoraireEntity();
        horaire.setHeureOuverture(LocalTime.of(9, 0));
        horaire.setHeureFermeture(LocalTime.of(18, 0));

        boolean ouvert = horaire.estOuvertSur(LocalTime.of(10, 0), LocalTime.of(17, 0));

        assertTrue(ouvert);
    }

    @Test
    void estOuvertSur_shouldReturnFalseWhenOutsideRange() {
        HoraireEntity horaire = new HoraireEntity();
        horaire.setHeureOuverture(LocalTime.of(9, 0));
        horaire.setHeureFermeture(LocalTime.of(18, 0));

        boolean ouvert = horaire.estOuvertSur(LocalTime.of(8, 0), LocalTime.of(19, 0));

        assertFalse(ouvert);
    }
}