package com.managertools.engineer;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record EngineerRequest(
        @NotBlank String name,
        @NotBlank String role,
        LocalDate joinedAt
) {}
