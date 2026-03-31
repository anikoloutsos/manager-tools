package com.managertools.engineer;

import java.time.LocalDate;

public record EngineerResponse(
        Long id,
        String name,
        String role,
        LocalDate joinedAt,
        LocalDate lastNoteDate
) {
    static EngineerResponse from(Engineer engineer) {
        LocalDate lastNoteDate = engineer.getNotes().isEmpty()
                ? null
                : engineer.getNotes().get(0).getMeetingDate();
        return new EngineerResponse(
                engineer.getId(),
                engineer.getName(),
                engineer.getRole(),
                engineer.getJoinedAt(),
                lastNoteDate
        );
    }
}
