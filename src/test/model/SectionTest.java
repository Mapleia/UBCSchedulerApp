package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    public Course cpsc210;
    public JsonReader reader;
    public Section cpsc210_101;
    public List<String> preferences = new ArrayList<>();

    @BeforeEach
    public void setup() {
        reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");
    }

    @Test
    public void testCreateSection() {
        try {
            cpsc210 = reader.readCourse("2020W", preferences);
        } catch (Exception e) {
            fail();
        }
        ArrayList<String> days = new ArrayList<>();
        days.add("TUE");
        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());
        assertEquals("18:00", cpsc210.getSectionsMap().get("CPSC 210 L1U").getStart().toString());
        assertEquals(days, cpsc210.getSectionsMap().get("CPSC 210 L1U").getDays());
    }

    @Test
    public void testToJson() {
        try {
            cpsc210 = reader.readCourse("2020W", preferences);
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

    @Test
    public void testCreateZonedDateTimes() {
        try {
            cpsc210 = reader.readCourse("2020W", preferences);
            cpsc210_101 = cpsc210.getSectionsMap().get("CPSC 210 101");
        } catch (Exception e) {
            fail();
        }

        assertEquals(cpsc210_101.getLocalDateList().get(0), LocalDate.of(2020, 9, 2));
        assertEquals(cpsc210_101.getLocalDateList().get(1), LocalDate.of(2020, 9, 4));
        assertEquals(cpsc210_101.getLocalDateList().get(2), LocalDate.of(2020, 9, 7));

    }

    @Test
    public void testStartIsNull() {}

    @Test
    public void testEndIsNull() {}

}
