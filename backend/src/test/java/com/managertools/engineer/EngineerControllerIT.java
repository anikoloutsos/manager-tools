package com.managertools.engineer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.managertools.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EngineerControllerIT extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EngineerRepository engineerRepository;

    @BeforeEach
    void setUp() {
        engineerRepository.deleteAll();
    }

    @Test
    void createEngineer_returnsCreated() throws Exception {
        var body = Map.of("name", "Alice", "role", "Engineer", "joinedAt", "2022-01-01");

        mockMvc.perform(post("/api/engineers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.role").value("Engineer"));
    }

    @Test
    void createEngineer_missingName_returns400() throws Exception {
        var body = Map.of("role", "Engineer");

        mockMvc.perform(post("/api/engineers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getEngineer_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/engineers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listEngineers_returnsAll() throws Exception {
        engineerRepository.save(new Engineer("Alice", "Engineer", LocalDate.now()));
        engineerRepository.save(new Engineer("Bob", "Senior Engineer", LocalDate.now()));

        mockMvc.perform(get("/api/engineers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateEngineer_success() throws Exception {
        Engineer saved = engineerRepository.save(new Engineer("Alice", "Engineer", LocalDate.now()));
        var body = Map.of("name", "Alice Updated", "role", "Senior Engineer", "joinedAt", "2022-01-01");

        mockMvc.perform(put("/api/engineers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.role").value("Senior Engineer"));
    }

    @Test
    void deleteEngineer_success() throws Exception {
        Engineer saved = engineerRepository.save(new Engineer("Alice", "Engineer", LocalDate.now()));

        mockMvc.perform(delete("/api/engineers/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/engineers/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
