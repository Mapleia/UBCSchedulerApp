package model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class OverviewTest {
    private Overview overview;

    @BeforeEach
    public void setup() {
        JsonReader reader = new JsonReader("./data/2020W/overview.json");
        try {
            overview = reader.readOverview();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetters() {
       assertTrue(overview.getDepArr().contains("CPSC"));
        assertTrue(overview.getCourses("CPSC").contains("CPSC 210"));
    }
}
