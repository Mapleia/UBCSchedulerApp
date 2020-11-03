package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    public Course cpsc210;
    public JsonReader reader;

    @BeforeEach
    public void setup() {
        reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
    }

    @Test
    public void testCreateSection() {
        try {
            cpsc210 = reader.readCourse();
        } catch (Exception e) {
            fail();
        }
        ArrayList<String> days = new ArrayList<>();
        days.add("TUE");
        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());
        assertEquals("18:00", cpsc210.getSectionsMap().get("CPSC 210 L1U").getStart());
        assertEquals(days, cpsc210.getSectionsMap().get("CPSC 210 L1U").getDays());
    }

    @Test
    public void testToJson() {
        try {
            cpsc210 = reader.readCourse();
        } catch (Exception e) {
            fail();
        }
        ArrayList<String> days = new ArrayList<>();
        days.add("TUE");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Full");
        jsonObject.put("section", "CPSC 210 L1U");
        jsonObject.put("activity", "Laboratory");
        jsonObject.put("term", "1");
        jsonObject.put("days", days);
        jsonObject.put("start", "18:00");
        jsonObject.put("end", "20:00");

        JSONObject test = cpsc210.getSectionsMap().get("CPSC 210 L1U").toJson();
        assertTrue(test.similar(jsonObject));

    }
}
