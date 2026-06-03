package fr.formation.poc.valeurs_mobilieres;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrdreBourseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listerTous_renvoie200EtJeuFictif() throws Exception {
        mockMvc.perform(get("/api/v1/ordres-bourse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codeClient").exists())
                .andExpect(jsonPath("$[0].codeIsin").exists());
    }
}
