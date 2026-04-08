package io.anikoloutsos.manager_tools.engineer;

import io.anikoloutsos.manager_tools.TestcontainersConfiguration;
import io.anikoloutsos.manager_tools.engineer.dto.CreateEngineerRequest;
import io.anikoloutsos.manager_tools.engineer.dto.EngineerResponse;
import io.anikoloutsos.manager_tools.engineer.dto.UpdateEngineerRequest;
import io.anikoloutsos.manager_tools.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EngineerControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EngineerRepository repository;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToApplicationContext(context).build();
        repository.deleteAll();
    }

    // --- GET /engineers ---

    @Test
    void getAll_returnsEmptyList_whenNoEngineers() {
        client.get().uri("/engineers")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EngineerResponse[].class)
                .consumeWith(result -> assertThat(result.getResponseBody()).isEmpty());
    }

    @Test
    void getAll_returnsAll_whenEngineersExist() {
        repository.save(buildEngineer("Alice"));
        repository.save(buildEngineer("Bob"));

        client.get().uri("/engineers")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EngineerResponse[].class)
                .consumeWith(result -> assertThat(result.getResponseBody()).hasSize(2));
    }

    // --- GET /engineers/{id} ---

    @Test
    void getById_returnsEngineer_whenFound() {
        Engineer saved = repository.save(buildEngineer("Alice"));

        client.get().uri("/engineers/" + saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EngineerResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().name()).isEqualTo("Alice");
                    assertThat(result.getResponseBody().id()).isEqualTo(saved.getId());
                });
    }

    @Test
    void getById_returns404_whenNotFound() {
        client.get().uri("/engineers/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    // --- POST /engineers ---

    @Test
    void create_returns201_withIdInResponse() {
        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(validCreateRequest("Charlie"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EngineerResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().id()).isNotNull();
                    assertThat(result.getResponseBody().name()).isEqualTo("Charlie");
                });
    }

    @Test
    void create_returns400_whenNameIsBlank() {
        CreateEngineerRequest request = new CreateEngineerRequest(
                "", EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, null, null
        );

        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    @Test
    void create_returns400_whenLevelIsNull() {
        CreateEngineerRequest request = new CreateEngineerRequest(
                "Dave", null, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, null, null
        );

        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    @Test
    void create_returns400_whenHourlyRateIsNegative() {
        CreateEngineerRequest request = new CreateEngineerRequest(
                "Dave", EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, new BigDecimal("-1.00"), null
        );

        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    @Test
    void create_returns400_whenDaysAtOfficeIsNegative() {
        CreateEngineerRequest request = new CreateEngineerRequest(
                "Dave", EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, null, -1
        );

        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    @Test
    void create_returns201_withNullableFieldsAbsent() {
        CreateEngineerRequest request = new CreateEngineerRequest(
                "Eve", EngineerLevel.ENGINEER_II, Squad.SQUAD_2, EmploymentType.EXTERNAL,
                null, null, null
        );

        client.post().uri("/engineers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EngineerResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().hireDate()).isNull();
                    assertThat(result.getResponseBody().hourlyRate()).isNull();
                    assertThat(result.getResponseBody().daysAtOffice()).isNull();
                });
    }

    // --- PUT /engineers/{id} ---

    @Test
    void update_returns204_whenFound() {
        Engineer saved = repository.save(buildEngineer("Frank"));
        UpdateEngineerRequest request = new UpdateEngineerRequest(
                "Frank Updated", EngineerLevel.ENGINEER_III, Squad.SQUAD_3, EmploymentType.INTERNAL,
                LocalDate.of(2023, 1, 15), new BigDecimal("75.00"), 3
        );

        client.put().uri("/engineers/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNoContent();

        Engineer updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Frank Updated");
        assertThat(updated.getLevel()).isEqualTo(EngineerLevel.ENGINEER_III);
        assertThat(updated.getSquad()).isEqualTo(Squad.SQUAD_3);
        assertThat(updated.getHireDate()).isEqualTo(LocalDate.of(2023, 1, 15));
    }

    @Test
    void update_returns404_whenNotFound() {
        UpdateEngineerRequest request = new UpdateEngineerRequest(
                "Ghost", EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, null, null
        );

        client.put().uri("/engineers/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    @Test
    void update_returns400_onValidationFailure() {
        Engineer saved = repository.save(buildEngineer("Hana"));
        UpdateEngineerRequest request = new UpdateEngineerRequest(
                "", EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                null, null, null
        );

        client.put().uri("/engineers/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    // --- DELETE /engineers/{id} ---

    @Test
    void delete_returns204_whenFound() {
        Engineer saved = repository.save(buildEngineer("Ivan"));

        client.delete().uri("/engineers/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_returns404_whenNotFound() {
        client.delete().uri("/engineers/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                    assertThat(result.getResponseBody().message()).isNotBlank();
                });
    }

    // --- Helpers ---

    private Engineer buildEngineer(String name) {
        return new Engineer(name, EngineerLevel.ENGINEER_I, Squad.SQUAD_1,
                LocalDate.of(2022, 6, 1), EmploymentType.INTERNAL, null, null);
    }

    private CreateEngineerRequest validCreateRequest(String name) {
        return new CreateEngineerRequest(
                name, EngineerLevel.ENGINEER_I, Squad.SQUAD_1, EmploymentType.INTERNAL,
                LocalDate.of(2022, 6, 1), new BigDecimal("50.00"), 2
        );
    }
}
