package com.example;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8080);

        // Define the route without any authentication
        get("/hello", (req, res) -> "Hello from CI/CD Pipeline i am PDDR!");
    }
}

