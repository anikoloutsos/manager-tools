package com.managertools.note;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        Long engineerId,
        String content,
        LocalDate meetingDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getEngineer().getId(),
                note.getContent(),
                note.getMeetingDate(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
