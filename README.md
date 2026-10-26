# Java Course — Lecture Demos

Demonstration code for the Java course lectures and lab assignments.
Each package is a self-contained example used during a presentation:
small enough to read on a slide, complete enough to run and discuss.

> This is teaching code. It focuses on one idea at a time and is kept minimal on purpose,
> so some production concerns (configuration, persistence, error recovery) are left out.

## Requirements

- JDK 17 or newer (the examples use records)
- Gradle (the wrapper is included, no separate installation needed)
- Any IDE with Gradle support; IntelliJ IDEA is used in the lectures

## Structure

| Package | Topic | What it demonstrates |
|---|---|---|
| `lec5` | Collections, `equals`/`hashCode`, `Comparable` | A `Room` record whose ordering is inconsistent with `equals`, and how this affects `TreeSet` compared to `HashSet` |
| `lec_interface` | Interfaces | Interface examples from the interfaces lecture |
| `org.example` | Unit testing | `StudentRegistry` covered by JUnit 5 tests: Arrange–Act–Assert, independent tests, testing exceptions |
| `genericrepo` | Generics and lambdas | A generic repository built on `Map`, a functional interface implemented with lambdas, `Optional`, a custom unchecked exception, SLF4J logging, and a benchmark comparing `Map` and `List` lookup |

