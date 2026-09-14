package common.utils;

import com.codeborne.selenide.Selenide;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtils {

    public static <T> T retry(
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts,
            long delayMillis) {

        T result;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            result = action.get();
            if (condition.test(result)) {
                return result;
            }
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

        }
        Selenide.refresh();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            result = action.get();
            if (condition.test(result)) {
                return result;
            }
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException(
                "Retry failed after " + maxAttempts + " attempts!"
        );
    }
}
