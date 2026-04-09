package io.anikoloutsos.manager_tools.note;

import jakarta.persistence.AttributeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class InstantToLocalDateConverter implements AttributeConverter<Instant, LocalDate> {

    public static Instant toUtcMidnight(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    @Override
    public LocalDate convertToDatabaseColumn(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    @Override
    public Instant convertToEntityAttribute(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
