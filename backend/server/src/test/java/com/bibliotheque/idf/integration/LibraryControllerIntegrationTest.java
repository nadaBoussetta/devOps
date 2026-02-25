package com.bibliotheque.idf.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import devOps.SampleDataJpaApplication;
import devOps.dtos.RechercheDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SampleDataJpaApplication.class)
@AutoConfigureMockMvc
class BibliothequeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllBibliotheques() throws Exception {
        mockMvc.perform(get("/api/bibliotheques"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetBibliothequeById_Success() throws Exception {
        mockMvc.perform(get("/api/bibliotheques/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").exists())
                .andExpect(jsonPath("$.adresse").exists());
    }

    @Test
    void testRechercherBibliotheques_Success() throws Exception {
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setHeureDebut("09:00");
        recherche.setHeureFin("18:00");
        recherche.setRayon(10.0);

        mockMvc.perform(post("/api/bibliotheques/recherche")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recherche)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testRechercherBibliotheques_ValidationError() throws Exception {
        RechercheDTO recherche = new RechercheDTO();

        mockMvc.perform(post("/api/bibliotheques/recherche")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recherche)))
                .andExpect(status().isBadRequest());
    }
}