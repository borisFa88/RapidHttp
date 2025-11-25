package rapidServer;

import org.rapidoid.http.Req;
import org.rapidoid.setup.On;


public class Rapidoid {
    public static void main(String[] args) {
        On.address("0.0.0.0");
        On.port(8000);
        On.get("/hello").plain("Hello, Rapidoid!"); // Handles GET requests to the root path
     //   On.get("/hello/{name}").plain((String name) -> "Hello, " + name + "!"); // Handles GET requests with a path parameter

        On.get("/hello/{name}").plain((Req req) -> {
            String name = req.param("name");
            return "Hello, " + name + "!";
        });

       On.post("/user").json((req) -> {
            User user = req.data(User.class);
            return "Hello " + user.name + ", age: " + user.age;
        });
       }

    public static class User {
        public String name;
        public int age;
    }
}
