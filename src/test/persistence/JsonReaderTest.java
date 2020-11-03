package persistence;

import org.junit.jupiter.api.BeforeEach;

public class JsonReaderTest {
    private JsonReader jsonReader;

    @BeforeEach
    public void setup() {
        jsonReader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
    }
}
