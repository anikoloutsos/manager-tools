package com.managertools.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.managertools.AbstractIntegrationTest;
import com.managertools.engineer.Engineer;
import com.managertools.engineer.EngineerRepository;
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

class NoteControllerIT extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EngineerRepository engineerRepository;
    @Autowired NoteRepository noteRepository;

    Engineer engineer;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        engineerRepository.deleteAll();
        engineer = engineerRepository.save(new Engineer("Alice", "Engineer", LocalDate.now()));
    }

    @Test
    void createNote_returnsCreated() throws Exception {
        var body = Map.of("content", "Discussed career goals", "meetingDate", "2024-03-15");

        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("Discussed career goals"))
                .andExpect(jsonPath("$.engineerId").value(engineer.getId()));
    }

    @Test
    void createNote_missingContent_returns400() throws Exception {
        var body = Map.of("meetingDate", "2024-03-15");

        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void createNote_engineerNotFound_returns404() throws Exception {
        var body = Map.of("content", "Test note", "meetingDate", "2024-03-15");

        mockMvc.perform(post("/api/engineers/999/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listNotes_returnsNewestFirst() throws Exception {
        var body1 = Map.of("content", "First meeting", "meetingDate", "2024-01-01");
        var body2 = Map.of("content", "Second meeting", "meetingDate", "2024-06-01");

        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body1)));
        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body2)));

        mockMvc.perform(get("/api/engineers/" + engineer.getId() + "/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Second meeting"))
                .andExpect(jsonPath("$[1].content").value("First meeting"));
    }

    @Test
    void summary_returnsAtMostFiveNotes() throws Exception {
        for (int i = 1; i <= 7; i++) {
            var body = Map.of("content", "Note " + i, "meetingDate", "2024-0" + (i < 10 ? "0" : "") + i + "-01");
            mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)));
        }

        mockMvc.perform(get("/api/engineers/" + engineer.getId() + "/notes/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void search_returnsMatchingNotes() throws Exception {
        var body1 = Map.of("content", "Discussed performance review", "meetingDate", "2024-03-01");
        var body2 = Map.of("content", "Talked about team events", "meetingDate", "2024-04-01");

        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body1)));
        mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body2)));

        mockMvc.perform(get("/api/engineers/" + engineer.getId() + "/notes/search?q=performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Discussed performance review"));
    }

    @Test
    void updateNote_success() throws Exception {
        var createBody = Map.of("content", "Original content", "meetingDate", "2024-03-01");
        var result = mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andReturn();

        Long noteId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        var updateBody = Map.of("content", "Updated content", "meetingDate", "2024-03-01");

        mockMvc.perform(put("/api/notes/" + noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void deleteNote_success() throws Exception {
        var createBody = Map.of("content", "To be deleted", "meetingDate", "2024-03-01");
        var result = mockMvc.perform(post("/api/engineers/" + engineer.getId() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andReturn();

        Long noteId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/notes/" + noteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/engineers/" + engineer.getId() + "/notes"))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
