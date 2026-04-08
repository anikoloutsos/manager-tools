package io.anikoloutsos.manager_tools.engineer.dto;

import io.anikoloutsos.manager_tools.engineer.EmploymentType;
import io.anikoloutsos.manager_tools.engineer.EngineerLevel;
import io.anikoloutsos.manager_tools.engineer.Squad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEngineerRequest(
        @NotBlank String name,
        @NotNull EngineerLevel level,
        @NotNull Squad squad,
        @NotNull EmploymentType employmentType,
        LocalDate hireDate,
        @PositiveOrZero BigDecimal hourlyRate,
        @Min(0) Integer daysAtOffice
) {
}
