package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CourseTest {
    public Course cpsc210;
    public JsonReader reader;

    @BeforeEach
    public void setup() {
        reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
    }

    @Test
    public void testSectionCreation() {
        try {
            cpsc210 = reader.readCourse();
        } catch (Exception e) {
            fail();
        }

        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());

    }
}
