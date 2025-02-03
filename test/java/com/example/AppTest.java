package com.example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import spark.Spark;

public class AppTest {

    @Test
    public void testHelloRoute() {
        // Start the server before tests
        Spark.awaitInitialization();
        
        // Make a request to the /hello route
        String response = TestUtil.getResponse("http://localhost:8080/hello");
        
        // Assert that the response is what we expect
        assertEquals("Hello from CI/CD Pipeline!", response);
        
        // Stop the server after tests
        Spark.stop();
    }
}
