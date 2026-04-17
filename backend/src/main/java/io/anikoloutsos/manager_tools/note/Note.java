package io.anikoloutsos.manager_tools.note;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "engineer_id", nullable = false)
    private UUID engineerId;

    @Convert(converter = InstantToLocalDateConverter.class)
    @Column(nullable = false)
    private Instant date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    protected Note() {
    }

    public Note(UUID engineerId, Instant date, String body) {
        this.engineerId = engineerId;
        this.date = date;
        this.body = body;
    }

    public UUID getId() { return id; }

    public UUID getEngineerId() { return engineerId; }

    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
