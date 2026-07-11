package org.bk.books;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public final class TimeTestData {
    public static Clock FIXED_CLOCK = Clock.fixed(
            LocalDateTime.of(2025, 03, 04, 0, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    public static Instant DEFAULT_CREATED_DATE = FIXED_CLOCK.instant().minus(1, ChronoUnit.DAYS);

    public static Instant DEFAULT_UPDATED_DATE = FIXED_CLOCK.instant().plus(1, ChronoUnit.DAYS);

}
