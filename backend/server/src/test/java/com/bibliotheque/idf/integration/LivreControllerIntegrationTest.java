package com.bibliotheque.idf.integration;

import org.junit.jupiter.api.Test;
import devOps.SampleDataJpaApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = SampleDataJpaApplication.class)
@AutoConfigureMockMvc
class LivreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRechercherLivre_Success() throws Exception {
        mockMvc.perform(get("/api/livres/recherche")
                        .param("titre", "Les Misérables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testRechercherLivre_TitreVide() throws Exception {
        mockMvc.perform(get("/api/livres/recherche")
                        .param("titre", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testRechercherLivre_LivreIntrouvable() throws Exception {
        mockMvc.perform(get("/api/livres/recherche")
                        .param("titre", "LivreQuiNExistePasXYZ123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
