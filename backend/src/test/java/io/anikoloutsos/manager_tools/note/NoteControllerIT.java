package io.anikoloutsos.manager_tools.note;

import io.anikoloutsos.manager_tools.TestcontainersConfiguration;
import io.anikoloutsos.manager_tools.engineer.Engineer;
import io.anikoloutsos.manager_tools.engineer.EmploymentType;
import io.anikoloutsos.manager_tools.engineer.EngineerLevel;
import io.anikoloutsos.manager_tools.engineer.EngineerRepository;
import io.anikoloutsos.manager_tools.engineer.Squad;
import io.anikoloutsos.manager_tools.exception.ErrorResponse;
import io.anikoloutsos.manager_tools.note.dto.CreateNoteRequest;
import io.anikoloutsos.manager_tools.note.dto.NoteResponse;
import io.anikoloutsos.manager_tools.note.dto.UpdateNoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class NoteControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToApplicationContext(context).build();
        noteRepository.deleteAll();
        engineerRepository.deleteAll();
    }

    // --- GET /engineers/{engineerId}/notes ---

    @Test
    void getAll_returnsEmptyList_whenNoNotes() {
        Engineer engineer = engineerRepository.save(buildEngineer());

        client.get().uri("/engineers/" + engineer.getId() + "/notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(NoteResponse[].class)
                .consumeWith(result -> assertThat(result.getResponseBody()).isEmpty());
    }

    @Test
    void getAll_returnsNotesOrderedByDateDescending() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant older = LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant newer = LocalDate.of(2024, 6, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        noteRepository.save(new Note(engineer.getId(), older, "Older note"));
        noteRepository.save(new Note(engineer.getId(), newer, "Newer note"));

        client.get().uri("/engineers/" + engineer.getId() + "/notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(NoteResponse[].class)
                .consumeWith(result -> {
                    NoteResponse[] body = result.getResponseBody();
                    assertThat(body).hasSize(2);
                    assertThat(body[0].body()).isEqualTo("Newer note");
                    assertThat(body[0].date()).isEqualTo(newer);
                    assertThat(body[1].body()).isEqualTo("Older note");
                    assertThat(body[1].date()).isEqualTo(older);
                });
    }

    @Test
    void getAll_returns404_whenEngineerNotFound() {
        client.get().uri("/engineers/" + UUID.randomUUID() + "/notes")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- GET /engineers/{engineerId}/notes/{id} ---

    @Test
    void getById_returnsNote_whenFound() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant date = LocalDate.of(2024, 3, 15).atStartOfDay(ZoneOffset.UTC).toInstant();
        Note saved = noteRepository.save(new Note(engineer.getId(), date, "My note"));

        client.get().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(NoteResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody().id()).isEqualTo(saved.getId());
                    assertThat(result.getResponseBody().body()).isEqualTo("My note");
                    assertThat(result.getResponseBody().engineerId()).isEqualTo(engineer.getId());
                    assertThat(result.getResponseBody().date()).isEqualTo(date);
                });
    }

    @Test
    void getById_returns404_whenNoteNotFound() {
        Engineer engineer = engineerRepository.save(buildEngineer());

        client.get().uri("/engineers/" + engineer.getId() + "/notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void getById_returns404_whenEngineerNotFound() {
        client.get().uri("/engineers/" + UUID.randomUUID() + "/notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- POST /engineers/{engineerId}/notes ---

    @Test
    void create_returns201_withNoteInResponse() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant date = LocalDate.of(2024, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant();
        CreateNoteRequest request = new CreateNoteRequest(date, "Great progress this week");

        client.post().uri("/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NoteResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody().id()).isNotNull();
                    assertThat(result.getResponseBody().engineerId()).isEqualTo(engineer.getId());
                    assertThat(result.getResponseBody().body()).isEqualTo("Great progress this week");
                    assertThat(result.getResponseBody().date()).isEqualTo(date);
                });
    }

    @Test
    void create_roundTrips_nonMidnightInstant_toUtcMidnight() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant nonMidnight = LocalDate.of(2024, 5, 10).atTime(14, 30).toInstant(ZoneOffset.UTC);
        Instant expectedMidnight = LocalDate.of(2024, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant();

        client.post().uri("/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateNoteRequest(nonMidnight, "Note with mid-day time"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NoteResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().date()).isEqualTo(expectedMidnight));
    }

    @Test
    void create_returns404_whenEngineerNotFound() {
        CreateNoteRequest request = new CreateNoteRequest(
                LocalDate.of(2024, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Some note"
        );

        client.post().uri("/engineers/" + UUID.randomUUID() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenBodyIsBlank() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        CreateNoteRequest request = new CreateNoteRequest(
                LocalDate.of(2024, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant(),
                ""
        );

        client.post().uri("/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenDateIsNull() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        CreateNoteRequest request = new CreateNoteRequest(null, "Some note");

        client.post().uri("/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenDateIsMalformed() {
        Engineer engineer = engineerRepository.save(buildEngineer());

        client.post().uri("/engineers/" + engineer.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("date", "not-a-timestamp", "body", "Some note"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- PUT /engineers/{engineerId}/notes/{id} ---

    @Test
    void update_returns204_andPersistsChanges() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant originalDate = LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Note saved = noteRepository.save(new Note(engineer.getId(), originalDate, "Original body"));

        Instant newDate = LocalDate.of(2024, 6, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        UpdateNoteRequest request = new UpdateNoteRequest(newDate, "Updated body");

        client.put().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNoContent();

        Note updated = noteRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getBody()).isEqualTo("Updated body");
        assertThat(updated.getDate()).isEqualTo(newDate);
    }

    @Test
    void update_returns404_whenNoteNotFound() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        UpdateNoteRequest request = new UpdateNoteRequest(
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Some body"
        );

        client.put().uri("/engineers/" + engineer.getId() + "/notes/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns404_whenEngineerNotFound() {
        UpdateNoteRequest request = new UpdateNoteRequest(
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Some body"
        );

        client.put().uri("/engineers/" + UUID.randomUUID() + "/notes/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns400_whenDateIsNull() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Note saved = noteRepository.save(new Note(
                engineer.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Original"
        ));
        UpdateNoteRequest request = new UpdateNoteRequest(null, "Updated");

        client.put().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns400_whenBodyIsBlank() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Note saved = noteRepository.save(new Note(
                engineer.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Original"
        ));
        UpdateNoteRequest request = new UpdateNoteRequest(
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                ""
        );

        client.put().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns400_whenDateIsMalformed() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Note saved = noteRepository.save(new Note(
                engineer.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Original"
        ));

        client.put().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("date", "not-a-timestamp", "body", "Updated"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- DELETE /engineers/{engineerId}/notes/{id} ---

    @Test
    void delete_returns204_andRemovesNote() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Note saved = noteRepository.save(new Note(
                engineer.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "To be deleted"
        ));

        client.delete().uri("/engineers/" + engineer.getId() + "/notes/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(noteRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_returns404_whenNoteNotFound() {
        Engineer engineer = engineerRepository.save(buildEngineer());

        client.delete().uri("/engineers/" + engineer.getId() + "/notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void delete_returns404_whenEngineerNotFound() {
        client.delete().uri("/engineers/" + UUID.randomUUID() + "/notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- Ownership mismatch ---

    @Test
    void getById_returns404_whenNoteDoesNotBelongToEngineer() {
        Engineer engineer1 = engineerRepository.save(buildEngineer());
        Engineer engineer2 = engineerRepository.save(buildEngineer());
        Note noteForEngineer1 = noteRepository.save(new Note(
                engineer1.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Engineer 1 note"
        ));

        client.get().uri("/engineers/" + engineer2.getId() + "/notes/" + noteForEngineer1.getId())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns404_whenNoteDoesNotBelongToEngineer() {
        Engineer engineer1 = engineerRepository.save(buildEngineer());
        Engineer engineer2 = engineerRepository.save(buildEngineer());
        Instant originalDate = LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Note noteForEngineer1 = noteRepository.save(new Note(engineer1.getId(), originalDate, "Engineer 1 note"));
        UpdateNoteRequest request = new UpdateNoteRequest(
                LocalDate.of(2024, 2, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Attempted update"
        );

        client.put().uri("/engineers/" + engineer2.getId() + "/notes/" + noteForEngineer1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());

        Note unchanged = noteRepository.findById(noteForEngineer1.getId()).orElseThrow();
        assertThat(unchanged.getBody()).isEqualTo("Engineer 1 note");
        assertThat(unchanged.getDate()).isEqualTo(originalDate);
    }

    @Test
    void delete_returns404_whenNoteDoesNotBelongToEngineer() {
        Engineer engineer1 = engineerRepository.save(buildEngineer());
        Engineer engineer2 = engineerRepository.save(buildEngineer());
        Note noteForEngineer1 = noteRepository.save(new Note(
                engineer1.getId(),
                LocalDate.of(2024, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                "Engineer 1 note"
        ));

        client.delete().uri("/engineers/" + engineer2.getId() + "/notes/" + noteForEngineer1.getId())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());

        assertThat(noteRepository.existsById(noteForEngineer1.getId())).isTrue();
    }

    // --- Helpers ---

    private Engineer buildEngineer() {
        return new Engineer("Alice", EngineerLevel.ENGINEER_I, Squad.SQUAD_1,
                LocalDate.of(2022, 6, 1), EmploymentType.INTERNAL, null, null);
    }
}
