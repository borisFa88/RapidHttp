package org.example;

import org.rapidoid.setup.On;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainApp  {
    public static void main(String[] args) {
        On.port(8000);
        On.get("/").plain("Hello, Rapidoid!"); // Handles GET requests to the root path
        On.get("/hello/{name}").plain((String name) -> "Hello, " + name + "!"); // Handles GET requests with a path parameter
        On.post("/user").json((req) -> {
            User user = req.data(User.class);
            return "Hello " + user.name + ", age: " + user.age;
        });
        }
    }
