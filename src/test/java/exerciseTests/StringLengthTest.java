package exerciseTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StringLengthTest {
    @ParameterizedTest
    @ValueSource(strings = {"Hello, world", "Str in 15symbol", "Very long string, more 15 symbols"})
    public void testStringLength(String str) {
        int differense = str.length() - 15;
        Assertions.assertTrue(differense > 0, "String's length is less 15 symbols.");
    }
}
