package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentRegistryTest {

    private StudentRegistry registry;

    @BeforeEach
    void setUp() {
        // A fresh instance per test keeps tests independent
        registry = new StudentRegistry();
    }

    @Test
    @DisplayName("New registry has no students")
    void newRegistryIsEmpty() {
        assertEquals(0, registry.count());
    }

    @Test
    @DisplayName("Adding a student increases the count")
    void addIncreasesCount() {
        // Arrange
        Student student = new Student("Іван", "Петренко", 2);

        // Act
        registry.add(student);

        // Assert
        assertEquals(1, registry.count());
    }

    @Test
    @DisplayName("Added student is stored")
    void addStoresStudent() {
        Student student = new Student("Іван", "Петренко", 2);

        registry.add(student);

        assertEquals(List.of(student), registry.all());
    }

    @Test
    @DisplayName("Students are kept in insertion order")
    void keepsInsertionOrder() {
        Student first = new Student("Іван", "Петренко", 2);
        Student second = new Student("Марія", "Коваленко", 1);

        registry.add(first);
        registry.add(second);

        assertEquals(List.of(first, second), registry.all());
    }

    @Test
    @DisplayName("Adding the same student twice is rejected")
    void rejectsDuplicate() {
        Student student = new Student("Іван", "Петренко", 2);
        registry.add(student);

        assertThrows(IllegalStateException.class, () -> registry.add(student));
    }

    @Test
    @DisplayName("Rejected duplicate does not change the count")
    void duplicateDoesNotChangeCount() {
        Student student = new Student("Іван", "Петренко", 2);
        registry.add(student);

        assertThrows(IllegalStateException.class, () -> registry.add(student));

        assertEquals(1, registry.count());
    }

    @Test
    @DisplayName("Students differing in any field are not duplicates")
    void differentStudentsAreNotDuplicates() {
        registry.add(new Student("Іван", "Петренко", 2));
        registry.add(new Student("Іван", "Петренко", 3));

        assertEquals(2, registry.count());
    }

    @Test
    @DisplayName("Adding null is rejected")
    void addRejectsNull() {
        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> registry.add(null));

        assertEquals("student must not be null", e.getMessage());
    }

    @Test
    @DisplayName("Rejected null does not change the state")
    void rejectedNullLeavesStateUnchanged() {
        registry.add(new Student("Іван", "Петренко", 2));

        assertThrows(NullPointerException.class, () -> registry.add(null));

        assertEquals(1, registry.count());
    }
}
