import org.example.User;
import org.junit.jupiter.api.*;
import org.rapidoid.http.*;
import org.rapidoid.json.JSON;
import org.rapidoid.setup.On;
import static org.junit.jupiter.api.Assertions.*;
import org.rapidoid.u.U;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class MainAppTest {

    @BeforeAll
    static void startServer() {
        On.setup().address("localhost").port(9999);
        On.post("/user").json((req) -> {
            User user = req.data(User.class);
            return "Hello " + user.name + ", age: " + user.age;
        });
    }

    @Test
    void testUserEndpoint() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Alice");
        payload.put("age", 25);
        String json = JSON.stringify(payload);

        HttpResp resp = HTTP.post("http://localhost:9999/user")
                .body(json.getBytes(StandardCharsets.UTF_8)) // ✅ fix
                .execute();

        assertEquals(200, resp.code());

        String actual = resp.body();
        if (actual.startsWith("\"") && actual.endsWith("\"")) {
            actual = actual.substring(1, actual.length() - 1);
        }
        assertEquals("Hello Alice, age: 25", actual);
    }
}
