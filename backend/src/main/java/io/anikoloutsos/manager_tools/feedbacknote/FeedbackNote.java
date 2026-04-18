package io.anikoloutsos.manager_tools.feedbacknote;

import io.anikoloutsos.manager_tools.note.InstantToLocalDateConverter;
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
@Table(name = "feedback_notes")
public class FeedbackNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "engineer_id")
    private UUID engineerId;

    @Convert(converter = InstantToLocalDateConverter.class)
    @Column(nullable = false)
    private Instant date;

    @Column
    private String giver;

    @Column(columnDefinition = "TEXT")
    private String situation;

    @Column(columnDefinition = "TEXT")
    private String task;

    @Column(columnDefinition = "TEXT")
    private String action;

    @Column(columnDefinition = "TEXT")
    private String result;

    protected FeedbackNote() {
    }

    public FeedbackNote(UUID engineerId, Instant date, String giver,
                        String situation, String task, String action, String result) {
        this.engineerId = engineerId;
        this.date = date;
        this.giver = giver;
        this.situation = situation;
        this.task = task;
        this.action = action;
        this.result = result;
    }

    public UUID getId() { return id; }

    public UUID getEngineerId() { return engineerId; }
    public void setEngineerId(UUID engineerId) { this.engineerId = engineerId; }

    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }

    public String getGiver() { return giver; }
    public void setGiver(String giver) { this.giver = giver; }

    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }

    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
