package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class StudentCsvReader {

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()                 // first line is the header
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build();

    public static List<Student> read(Path path) throws IOException {
        List<Student> students = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = FORMAT.parse(reader)) {

            for (CSVRecord record : parser) {
                try {
                    students.add(toStudent(record));
                } catch (IllegalArgumentException | IllegalStateException
                         | DateTimeParseException | NullPointerException e) {
                    System.err.printf("Skipping line %d: %s (%s)%n",
                            parser.getCurrentLineNumber(), record, e.getMessage());
                }
            }
        }

        return students;
    }

    private static Student toStudent(CSVRecord record) {
        // Access by header name instead of index
        String firstName = record.get("firstName");
        String lastName = record.get("lastName");
        int course = Integer.parseInt(record.get("course"));

        String date = record.isSet("enrollmentDate") ? record.get("enrollmentDate") : "";
        return date.isEmpty()
                ? new Student(firstName, lastName, course)
                : new Student(firstName, lastName, course, LocalDate.parse(date));
    }
}
