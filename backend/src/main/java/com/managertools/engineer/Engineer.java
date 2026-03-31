package com.managertools.engineer;

import com.managertools.note.Note;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "engineers")
public class Engineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String role;

    private LocalDate joinedAt;

    @OneToMany(mappedBy = "engineer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("meetingDate DESC")
    private List<Note> notes = new ArrayList<>();

    public Engineer() {}

    public Engineer(String name, String role, LocalDate joinedAt) {
        this.name = name;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }
    public List<Note> getNotes() { return notes; }
}
