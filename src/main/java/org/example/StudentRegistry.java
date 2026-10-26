package org.example;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In-memory registry of students, preserving insertion order.
 *
 * <p>This class is not thread-safe.
 */
public class StudentRegistry {

    private static final Logger log = Logger.getLogger(StudentRegistry.class.getName());

    private final Set<Student> students = new LinkedHashSet<>();

    /**
     * Adds a student to the registry.
     *
     * <p>Two students are considered the same when all their fields are equal,
     * since {@link Student} is a record.
     *
     * @param student the student to add, not {@code null}
     * @throws NullPointerException  if {@code student} is {@code null}
     * @throws IllegalStateException if an equal student is already registered
     */
    public void add(Student student) {
        Objects.requireNonNull(student, "student must not be null");

        log.log(Level.FINE, "Adding student: course={0}", student.course());

        if (!students.add(student)) {
            throw new IllegalStateException("student is already in the registry");
        }

        log.log(Level.INFO, "Student added: course={0} total={1}",
                new Object[]{student.course(), students.size()});
    }

    /**
     * Returns the number of registered students.
     */
    public int count() {
        return students.size();
    }

    /**
     * Returns all registered students in insertion order.
     *
     * @return an unmodifiable copy; changes to it do not affect the registry
     */
    public List<Student> all() {
        return List.copyOf(students);
    }
}