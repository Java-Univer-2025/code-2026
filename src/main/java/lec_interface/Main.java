package lec_interface;

@FunctionalInterface
interface Calculator {
    int apply(int a, int b);
}

public class Main {

    private final String name = "Main";

    public static void main(String[] args) {
        new Main().demo();
    }

    void demo() {
        // 1. Syntax: anonymous class spells out the type, method name, parameter types and @Override
        Calculator addAnon = new Calculator() {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
        };

        // Lambda: only parameters and body, everything else is inferred from Calculator
        Calculator addLambda = (a, b) -> a + b;

        System.out.println("Anonymous class: " + addAnon.apply(2, 3));
        System.out.println("Lambda: " + addLambda.apply(2, 3));

        // 2. `this`: anonymous class refers to itself, lambda refers to the enclosing object
        Calculator anonThis = new Calculator() {
            @Override
            public int apply(int a, int b) {
                System.out.println("this in anonymous class: " + this.getClass().getName());
                return a + b;
            }
        };
        Calculator lambdaThis = (a, b) -> {
            System.out.println("this in lambda: " + this.getClass().getName() + ", name = " + this.name);
            return a + b;
        };
        anonThis.apply(1, 1);
        lambdaThis.apply(1, 1);

        // 3. State: anonymous class can have its own fields, lambda cannot
        Calculator counting = new Calculator() {
            private int calls = 0;

            @Override
            public int apply(int a, int b) {
                calls++;
                System.out.println("calls: " + calls);
                return a + b;
            }
        };
        counting.apply(1, 2);
        counting.apply(3, 4);

        // 4. Scope: lambda shares the enclosing scope, anonymous class creates a new one
        int a = 10;
        // Calculator broken = (a, b) -> a + b; // compile error: variable a is already defined
        Calculator shadowing = new Calculator() {
            @Override
            public int apply(int a, int b) { // OK: parameter a shadows the local variable a
                return a + b;
            }
        };
        System.out.println("Shadowing in anonymous class: " + shadowing.apply(1, 2));
    }
}