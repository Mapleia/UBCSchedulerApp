package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonWriterTest {
    private JsonWriter jsonWriter;

    @BeforeEach
    public void setup() {
        jsonWriter = new JsonWriter("testMe");
    }
}
