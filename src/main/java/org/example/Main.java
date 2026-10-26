package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Object {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    static {
        try (InputStream is = new FileInputStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }



    public static void main(String[] args) {
        Student s1  = createStudent("Іван", "Петренко", 2);
        Student s2 = createStudent("Марія", "Коваленко", 4);
        StudentRegistry registry = new StudentRegistry();
        addStudent(registry, "Іван", "Петренко", 2);
        addStudent(registry, "Іван", "Петренко", 2);   // duplicate
        addStudent(registry, "Марія", "Коваленко", 9); // invalid course

        log.log(Level.INFO, "Registry contains {0} students", registry.count());
    }

    private static void addStudent(StudentRegistry registry, String firstName, String lastName, int course) {
        try {
            registry.add(new Student(firstName, lastName, course));
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.log(Level.WARNING, "Student not added: reason={0}", e.getMessage());
        }
    }

    private static Student createStudent(String firstName, String lastName, int course) {
        log.log(Level.FINE, "Creating student: course={0}", course);

        try {
            Student student = new Student(firstName, lastName, course);
            log.log(Level.INFO, "Student created: course={0} enrolled={1}",
                    new Object[]{student.course(), student.enrollmentDate()});
            return student;
        } catch (IllegalArgumentException | NullPointerException e) {
            log.log(Level.FINE, "Student rejected: course={0} reason={1}",
                    new Object[]{course, e.getMessage()});
            throw e;
        }
    }
//        List<Student> students = null;
//        try {
//            students = StudentCsvReaderPlain.read(Path.of("students.csv"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        students.forEach(System.out::println);


}
