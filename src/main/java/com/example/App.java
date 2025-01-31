package com.example;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8080);
        get("/hello", (req, res) -> "Hello from CI/CD Pipeline!");
    }
}