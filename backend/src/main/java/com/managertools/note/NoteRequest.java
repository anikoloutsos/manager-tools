package com.managertools.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record NoteRequest(
        @NotBlank String content,
        @NotNull LocalDate meetingDate
) {}
