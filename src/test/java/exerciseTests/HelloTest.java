package exerciseTests;

import org.junit.jupiter.api.Test;

public class HelloTest {
    private String name = "Мария Голдина";

    @Test
    public void testHelloWithName(){
        System.out.println("Hello from " + name);
    }
}
