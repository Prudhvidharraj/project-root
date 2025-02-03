package com.example;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8080);

        // Basic authentication filter
        before((req, res) -> {
            String authorization = req.headers("Authorization");
            if (authorization == null || !authorization.equals("Basic c2Fua2l0OjEyMzQ1Ng==")) {  // base64-encoded 'sangit:123456'
                halt(401, "Unauthorized");
            }
        });

        // Define the route
        get("/hello", (req, res) -> "Hello from CI/CD Pipeline!");
    }
}
