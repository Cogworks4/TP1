package tests;

import java.util.function.Supplier;

public class TestHelper {
    private static int passed = 0;
    private static int failed = 0;

    public static void assertTrue(boolean cond, String msg) {
        if (cond) {
            passed++;
            System.out.println("PASS: " + msg);
        } else {
            failed++;
            System.out.println("FAIL: " + msg);
        }
    }

    public static <T extends Throwable> void expectException(Class<T> exClass, Runnable fn, String msg) {
        try {
            fn.run();
            failed++;
            System.out.println("FAIL: " + msg + " (no exception thrown)");
        } catch (Throwable t) {
            if (exClass.isInstance(t)) {
                passed++;
                System.out.println("PASS: " + msg + " (threw " + t.getClass().getSimpleName() + ")");
            } else {
                failed++;
                System.out.println("FAIL: " + msg + " (threw " + t.getClass().getSimpleName() + ")");
                t.printStackTrace(System.out);
            }
        }
    }

    public static void summary() {
        System.out.println("\n==== TEST SUMMARY ====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
