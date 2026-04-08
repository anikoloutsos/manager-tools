package io.anikoloutsos.manager_tools.engineer.dto;

import io.anikoloutsos.manager_tools.engineer.EmploymentType;
import io.anikoloutsos.manager_tools.engineer.EngineerLevel;
import io.anikoloutsos.manager_tools.engineer.Squad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EngineerResponse(
        UUID id,
        String name,
        EngineerLevel level,
        Squad squad,
        LocalDate hireDate,
        EmploymentType employmentType,
        BigDecimal hourlyRate,
        Integer daysAtOffice
) {
}
