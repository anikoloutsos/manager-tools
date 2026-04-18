package io.anikoloutsos.manager_tools.feedbacknote;

import io.anikoloutsos.manager_tools.TestcontainersConfiguration;
import io.anikoloutsos.manager_tools.engineer.Engineer;
import io.anikoloutsos.manager_tools.engineer.EmploymentType;
import io.anikoloutsos.manager_tools.engineer.EngineerLevel;
import io.anikoloutsos.manager_tools.engineer.EngineerRepository;
import io.anikoloutsos.manager_tools.engineer.Squad;
import io.anikoloutsos.manager_tools.exception.ErrorResponse;
import io.anikoloutsos.manager_tools.feedbacknote.dto.CreateFeedbackNoteRequest;
import io.anikoloutsos.manager_tools.feedbacknote.dto.FeedbackNoteResponse;
import io.anikoloutsos.manager_tools.feedbacknote.dto.UpdateFeedbackNoteRequest;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FeedbackNoteControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FeedbackNoteRepository feedbackNoteRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToApplicationContext(context).build();
        feedbackNoteRepository.deleteAll();
        engineerRepository.deleteAll();
    }

    // --- GET /feedback-notes ---

    @Test
    void getAll_returnsEmptyList_whenNoFeedbackNotes() {
        client.get().uri("/feedback-notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse[].class)
                .consumeWith(result -> assertThat(result.getResponseBody()).isEmpty());
    }

    @Test
    void getAll_returnsFeedbackNotesOrderedByDateDescending() {
        Instant older = dateAt(2024, 1, 1);
        Instant newer = dateAt(2024, 6, 1);
        feedbackNoteRepository.save(buildNote(null, older, "Older"));
        feedbackNoteRepository.save(buildNote(null, newer, "Newer"));

        client.get().uri("/feedback-notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse[].class)
                .consumeWith(result -> {
                    FeedbackNoteResponse[] body = result.getResponseBody();
                    assertThat(body).hasSize(2);
                    assertThat(body[0].situation()).isEqualTo("Newer");
                    assertThat(body[0].date()).isEqualTo(newer);
                    assertThat(body[1].situation()).isEqualTo("Older");
                    assertThat(body[1].date()).isEqualTo(older);
                });
    }

    @Test
    void getAll_includesNotesWithNullEngineerId() {
        feedbackNoteRepository.save(buildNote(null, dateAt(2024, 2, 1), "Unlinked feedback"));

        client.get().uri("/feedback-notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse[].class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).hasSize(1);
                    assertThat(result.getResponseBody()[0].engineerId()).isNull();
                    assertThat(result.getResponseBody()[0].situation()).isEqualTo("Unlinked feedback");
                });
    }

    // --- GET /feedback-notes/{id} ---

    @Test
    void getById_returnsFeedbackNote_whenFound() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant date = dateAt(2024, 3, 15);
        FeedbackNote saved = feedbackNoteRepository.save(new FeedbackNote(
                engineer.getId(), date, "Alice",
                "During sprint review", "Present the roadmap",
                "She walked the team through milestones", "Team aligned on priorities"
        ));

        client.get().uri("/feedback-notes/" + saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse.class)
                .consumeWith(result -> {
                    FeedbackNoteResponse body = result.getResponseBody();
                    assertThat(body.id()).isEqualTo(saved.getId());
                    assertThat(body.engineerId()).isEqualTo(engineer.getId());
                    assertThat(body.date()).isEqualTo(date);
                    assertThat(body.giver()).isEqualTo("Alice");
                    assertThat(body.situation()).isEqualTo("During sprint review");
                    assertThat(body.task()).isEqualTo("Present the roadmap");
                    assertThat(body.action()).isEqualTo("She walked the team through milestones");
                    assertThat(body.result()).isEqualTo("Team aligned on priorities");
                });
    }

    @Test
    void getById_returns404_whenNotFound() {
        client.get().uri("/feedback-notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- GET /engineers/{engineerId}/feedback-notes ---

    @Test
    void getByEngineerId_returnsOnlyThatEngineersNotes_orderedByDateDesc() {
        Engineer engineer1 = engineerRepository.save(buildEngineer());
        Engineer engineer2 = engineerRepository.save(buildEngineer());
        Instant older = dateAt(2024, 1, 1);
        Instant newer = dateAt(2024, 5, 1);
        feedbackNoteRepository.save(buildNote(engineer1.getId(), older, "E1 older"));
        feedbackNoteRepository.save(buildNote(engineer1.getId(), newer, "E1 newer"));
        feedbackNoteRepository.save(buildNote(engineer2.getId(), dateAt(2024, 3, 1), "E2 note"));
        feedbackNoteRepository.save(buildNote(null, dateAt(2024, 4, 1), "Unlinked"));

        client.get().uri("/engineers/" + engineer1.getId() + "/feedback-notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse[].class)
                .consumeWith(result -> {
                    FeedbackNoteResponse[] body = result.getResponseBody();
                    assertThat(body).hasSize(2);
                    assertThat(body[0].situation()).isEqualTo("E1 newer");
                    assertThat(body[1].situation()).isEqualTo("E1 older");
                });
    }

    @Test
    void getByEngineerId_returnsEmpty_whenEngineerHasNoNotes() {
        Engineer engineer = engineerRepository.save(buildEngineer());

        client.get().uri("/engineers/" + engineer.getId() + "/feedback-notes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FeedbackNoteResponse[].class)
                .consumeWith(result -> assertThat(result.getResponseBody()).isEmpty());
    }

    // --- POST /feedback-notes ---

    @Test
    void create_returns201_withOnlySituation() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        Instant date = dateAt(2024, 5, 10);
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                engineer.getId(), date, "Alice", "Standup feedback", null, null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeedbackNoteResponse.class)
                .consumeWith(result -> {
                    FeedbackNoteResponse body = result.getResponseBody();
                    assertThat(body.id()).isNotNull();
                    assertThat(body.engineerId()).isEqualTo(engineer.getId());
                    assertThat(body.giver()).isEqualTo("Alice");
                    assertThat(body.situation()).isEqualTo("Standup feedback");
                    assertThat(body.task()).isNull();
                    assertThat(body.action()).isNull();
                    assertThat(body.result()).isNull();
                });
    }

    @Test
    void create_returns201_withAllStarFields() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                engineer.getId(), dateAt(2024, 5, 10), "Bob",
                "S", "T", "A", "R"
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeedbackNoteResponse.class)
                .consumeWith(result -> {
                    FeedbackNoteResponse body = result.getResponseBody();
                    assertThat(body.situation()).isEqualTo("S");
                    assertThat(body.task()).isEqualTo("T");
                    assertThat(body.action()).isEqualTo("A");
                    assertThat(body.result()).isEqualTo("R");
                });
    }

    @Test
    void create_returns201_withNullEngineerId() {
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                null, dateAt(2024, 5, 10), "Bob", null, null, "Helped a junior debug", null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeedbackNoteResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody().engineerId()).isNull();
                    assertThat(result.getResponseBody().action()).isEqualTo("Helped a junior debug");
                });
    }

    @Test
    void create_returns404_whenEngineerIdUnknown() {
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                UUID.randomUUID(), dateAt(2024, 5, 10), null, "something", null, null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns404_whenEngineerIdUnknown() {
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(null, dateAt(2024, 1, 1), "original"));
        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                UUID.randomUUID(), dateAt(2024, 1, 1), null, "still valid", null, null, null
        );

        client.put().uri("/feedback-notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_roundTrips_nonMidnightInstant_toUtcMidnight() {
        Instant nonMidnight = LocalDate.of(2024, 5, 10).atTime(14, 30).toInstant(ZoneOffset.UTC);
        Instant expectedMidnight = dateAt(2024, 5, 10);
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                null, nonMidnight, null, "Mid-day", null, null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeedbackNoteResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().date()).isEqualTo(expectedMidnight));
    }

    @Test
    void create_returns400_whenAllStarFieldsAreBlank() {
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                null, dateAt(2024, 5, 10), "Alice", "", "  ", null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenAllStarFieldsAreNull() {
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                null, dateAt(2024, 5, 10), "Alice", null, null, null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenDateIsNull() {
        CreateFeedbackNoteRequest request = new CreateFeedbackNoteRequest(
                null, null, null, "something", null, null, null
        );

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void create_returns400_whenDateIsMalformed() {
        Map<String, Object> body = new HashMap<>();
        body.put("date", "not-a-timestamp");
        body.put("situation", "something");

        client.post().uri("/feedback-notes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- PUT /feedback-notes/{id} ---

    @Test
    void update_returns204_andPersistsChanges() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(engineer.getId(), dateAt(2024, 1, 1), "Old situation"));

        Instant newDate = dateAt(2024, 6, 1);
        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                engineer.getId(), newDate, "Carol",
                "New situation", "New task", "New action", "New result"
        );

        client.put().uri("/feedback-notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNoContent();

        FeedbackNote updated = feedbackNoteRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getDate()).isEqualTo(newDate);
        assertThat(updated.getGiver()).isEqualTo("Carol");
        assertThat(updated.getSituation()).isEqualTo("New situation");
        assertThat(updated.getTask()).isEqualTo("New task");
        assertThat(updated.getAction()).isEqualTo("New action");
        assertThat(updated.getResult()).isEqualTo("New result");
    }

    @Test
    void update_canClearEngineerLink_bySettingEngineerIdToNull() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(engineer.getId(), dateAt(2024, 1, 1), "linked"));

        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                null, dateAt(2024, 1, 1), null, "linked", null, null, null
        );

        client.put().uri("/feedback-notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(feedbackNoteRepository.findById(saved.getId()).orElseThrow().getEngineerId()).isNull();
    }

    @Test
    void update_returns404_whenNotFound() {
        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                null, dateAt(2024, 1, 1), null, "s", null, null, null
        );

        client.put().uri("/feedback-notes/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns400_whenAllStarFieldsAreBlank() {
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(null, dateAt(2024, 1, 1), "original"));

        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                null, dateAt(2024, 1, 1), "Alice", "", "   ", null, null
        );

        client.put().uri("/feedback-notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    @Test
    void update_returns400_whenDateIsNull() {
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(null, dateAt(2024, 1, 1), "original"));
        UpdateFeedbackNoteRequest request = new UpdateFeedbackNoteRequest(
                null, null, null, "still valid STAR", null, null, null
        );

        client.put().uri("/feedback-notes/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- DELETE /feedback-notes/{id} ---

    @Test
    void delete_returns204_andRemovesFeedbackNote() {
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(null, dateAt(2024, 1, 1), "to delete"));

        client.delete().uri("/feedback-notes/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(feedbackNoteRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_returns404_whenNotFound() {
        client.delete().uri("/feedback-notes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> assertThat(result.getResponseBody().message()).isNotBlank());
    }

    // --- CASCADE behavior ---

    @Test
    void deletingEngineer_cascadeDeletesTheirFeedbackNotes() {
        Engineer engineer = engineerRepository.save(buildEngineer());
        FeedbackNote saved = feedbackNoteRepository.save(
                buildNote(engineer.getId(), dateAt(2024, 1, 1), "will cascade"));

        engineerRepository.deleteById(engineer.getId());

        assertThat(feedbackNoteRepository.existsById(saved.getId())).isFalse();
    }

    // --- Helpers ---

    private static Instant dateAt(int year, int month, int day) {
        return LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private static FeedbackNote buildNote(UUID engineerId, Instant date, String situation) {
        return new FeedbackNote(engineerId, date, null, situation, null, null, null);
    }

    private static Engineer buildEngineer() {
        return new Engineer("Alice", EngineerLevel.ENGINEER_I, Squad.SQUAD_1,
                LocalDate.of(2022, 6, 1), EmploymentType.INTERNAL, null, null);
    }
}
