package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class StudentCsvReaderPlain {

    private static final Logger log = LoggerFactory.getLogger(StudentCsvReaderPlain.class);

    private static final String DELIMITER = ",";

    public static List<Student> read(Path path) throws IOException {
        log.info("Reading students from {}", path);

        List<Student> students = new ArrayList<>();
        int skipped = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // skip header
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    students.add(parseLine(line));
                    log.debug("Line {} parsed", lineNumber);
                } catch (IllegalArgumentException | DateTimeParseException | NullPointerException e) {
                    // NumberFormatException is a subclass of IllegalArgumentException
                    // System.err.printf("Skipping line %d: %s (%s)%n", lineNumber, line, e.getMessage());
                    skipped++;
                    log.warn("Skipping line {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to read {}", path, e);
            throw e;
        }

        log.info("Import finished: parsed={} skipped={}", students.size(), skipped);
        return students;
    }

    private static Student parseLine(String line) {
        // -1 keeps trailing empty values, so "a,b,2," gives 4 columns
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < 3 || parts.length > 4) {
            throw new IllegalArgumentException("expected 3 or 4 columns, got " + parts.length);
        }

        String firstName = parts[0];
        String lastName = parts[1];
        int course = Integer.parseInt(parts[2].strip());

        // Date column is optional: missing or empty -> alternative constructor (today)
        if (parts.length == 3 || parts[3].isBlank()) {
            return new Student(firstName, lastName, course);
        }

        LocalDate enrollmentDate = LocalDate.parse(parts[3].strip()); // ISO format: yyyy-MM-dd
        return new Student(firstName, lastName, course, enrollmentDate);
    }

    public static void main(String[] args) throws IOException {
        List<Student> students = read(Path.of("students.csv"));
        students.forEach(System.out::println);
    }
}