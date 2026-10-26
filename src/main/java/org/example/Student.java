package org.example;

import java.time.LocalDate;
import java.util.Objects;

public record Student(String firstName, String lastName, int course, LocalDate enrollmentDate) {

    // Compact canonical constructor: validation and normalization only,
    // fields are assigned automatically after this block
    public Student {
        Objects.requireNonNull(firstName, "firstName must not be null");
        Objects.requireNonNull(lastName, "lastName must not be null");
        Objects.requireNonNull(enrollmentDate, "enrollmentDate must not be null");

        if (course < 1 || course > 6) {
            throw new IllegalArgumentException("course must be between 1 and 6");
        }
        if (enrollmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("enrollmentDate cannot be in the future");
        }

        // Parameters can be reassigned before the implicit field assignment
        firstName = firstName.strip();
        lastName = lastName.strip();
    }

    // Alternative constructor: enrollmentDate defaults to today
    public Student(String firstName, String lastName, int course) {
        this(firstName, lastName, course, LocalDate.now());
    }
}