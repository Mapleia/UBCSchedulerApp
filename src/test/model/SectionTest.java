package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    public Course cpsc210;
    public JsonReader reader;
    public Section cpsc210_101;
    public List<String> preferences = new LinkedList<>();

    @BeforeEach
    public void setup() {
        reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");

        try {
            cpsc210 = reader.readCourse("2020W", preferences);
            SectionSorter.sortSections(cpsc210);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateSection() {
        ArrayList<String> days = new ArrayList<>();
        days.add("TUE");
        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());
        assertEquals("18:00", cpsc210.getSectionsMap().get("CPSC 210 L1U").getStart().toString());
        assertEquals("CPSC 210", cpsc210.getSectionsMap().get("CPSC 210 L1U").getCourse());
        assertEquals(cpsc210.getSectionsMap().get("CPSC 210 L1U").getDays(), days);
        assertEquals(cpsc210.getSectionsMap().get("CPSC 210 L1U").getTerm(), "1");
    }

    @Test
    public void testToJson() {
        ArrayList<String> days = new ArrayList<>();
        days.add("TUE");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Full");
        jsonObject.put("section", "CPSC 210 L1U");
        jsonObject.put("course", "CPSC 210");
        jsonObject.put("activity", "Laboratory");
        jsonObject.put("term", "1");
        jsonObject.put("days", days);
        jsonObject.put("start", "18:00");
        jsonObject.put("end", "19:50");

        JSONObject test = cpsc210.getSectionsMap().get("CPSC 210 L1U").toJson();
        assertTrue(test.similar(jsonObject));

    }

    @Test
    public void testCreateZonedDateTimes() {
        cpsc210_101 = cpsc210.getSectionsMap().get("CPSC 210 101");

        assertEquals(cpsc210_101.getFirstWeekList().get(0), LocalDate.of(2020, 9, 2));
        assertEquals(cpsc210_101.getFirstWeekList().get(1), LocalDate.of(2020, 9, 4));
        assertEquals(cpsc210_101.getFirstWeekList().get(2), LocalDate.of(2020, 9, 7));

    }

    @Test
    public void testStartEndIsNull() {
        Course biol200;
        JsonReader readerNew = new JsonReader("./data/2020W/BIOL/BIOL 200.json");
        try {
            biol200 = readerNew.readCourse("2020W", preferences);
            SectionSorter.sortSections(biol200);
            Section section = biol200.getSectionsMap().get("BIOL 200 000");
            assertEquals("N/A", section.getTimeSpan());

        } catch (Exception e) {
            fail();
        }


    }

    @Test
    public void testTimeSpanNA() {
        assertEquals("N/A", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(8, 0),
                null, "2020W").getTimeSpan());
        assertEquals("N/A", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), null,
                LocalTime.of(8, 0), "2020W").getTimeSpan());
    }

    @Test
    public void testTimeSpanMorning() {
        assertEquals("MORNING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(8, 0),
                LocalTime.of(9, 0),"2020W").getTimeSpan());
        assertEquals("MORNING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(11, 0),
                LocalTime.of(12, 0),"2020W").getTimeSpan());
        assertEquals("MORNING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(11, 58),
                LocalTime.of(13, 0),"2020W").getTimeSpan());
        assertEquals("MORNING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(11, 59),
                LocalTime.of(13, 0),"2020W").getTimeSpan());
        assertEquals("MORNING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(0, 0),
                LocalTime.of(1, 0),"2020W").getTimeSpan());
    }

    @Test
    public void testTimeSpanAfternoon() {
        assertEquals("AFTERNOON", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(12, 0),
                LocalTime.of(13, 0),"2020W").getTimeSpan());
        assertEquals("AFTERNOON", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(12, 0),
                LocalTime.of(13, 0),"2020W").getTimeSpan());
        assertEquals("AFTERNOON", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(17, 59),
                LocalTime.of(19, 0),"2020W").getTimeSpan());
    }

    @Test
    public void testTimeSpanEvening() {
        assertEquals("EVENING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(18, 0),
                LocalTime.of(19, 0),"2020W").getTimeSpan());
        assertEquals("EVENING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(20, 0),
                LocalTime.of(21, 0),"2020W").getTimeSpan());
        assertEquals("EVENING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(23, 0),
                LocalTime.of(23, 1),"2020W").getTimeSpan());
        assertEquals("EVENING", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(23, 59),
                LocalTime.of(0, 0),"2020W").getTimeSpan());
    }

    @Test
    public void testGetStartStr() {
        assertEquals("18:00", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(18, 0),
                LocalTime.of(19, 0),"2020W").getStartStr());
        assertEquals("N/A", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), null,
                LocalTime.of(19, 0),"2020W").getStartStr());
    }

    @Test
    public void testGetEndStr() {
        assertEquals("19:00", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(18, 0),
                LocalTime.of(19, 0),"2020W").getEndStr());
        assertEquals("N/A", new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), LocalTime.of(18, 0),
                null,"2020W").getEndStr());
    }
}
