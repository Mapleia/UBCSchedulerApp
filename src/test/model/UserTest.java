package model;

import exceptions.NoCourseFound;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;
    List<String> preferences = new LinkedList<>();


    @BeforeEach
    public void setup() {
        user = new User("2020W");
        preferences.add("Afternoon".toUpperCase());
        preferences.add("Evening".toUpperCase());
        preferences.add("Morning".toUpperCase());
        user.setPreferences(preferences);
    }

    @Test
    public void testToJson() {
        List<String> courses = new ArrayList<>();
        courses.add("CPSC 210");
        courses.add("BIOL 112");
        try {
            user.addCourses(courses);
            user.createTimeTable();
        } catch (Exception e) {
            fail();
        }
        JsonWriter writer = new JsonWriter("testUserWithCourses");

        try {
            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testUserWithCourses.json");
            JSONObject obj = new JSONObject(reader.readFile());
            assertTrue(obj.similar(user.toJson()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTimeTable() {
        assertEquals(user.getFinalTimeTable(), new HashMap<String, ArrayList<Section>>());
    }

    @Test
    public void testErrorLog() {
        assertEquals(user.getErrorLog(), new ArrayList<>());
    }

    @Test
    public void testAddSectionsToUser() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithCourses.json");
        try {
            user = jsonReader.readUser();
        } catch (Exception e) {
            fail();
        }

        assertEquals("BIOL 112 101",
                user.getFinalTimeTable().get("1").get(0).getSection());
        assertEquals("BIOL 112 T01",
                user.getFinalTimeTable().get("1").get(1).getSection());
        assertEquals("CPSC 210 201",
                user.getFinalTimeTable().get("2").get(0).getSection());
        assertEquals("CPSC 210 L2A",
                user.getFinalTimeTable().get("2").get(1).getSection());
        List<String> list = new ArrayList<>();
        list.add("CPSC 210");
        list.add("BIOL 112");
        assertEquals(user.getCourseList(), list);
        assertEquals("2020W", user.getTerm());
    }

    @Test
    public void testAddCoursesException() {
        List<String> list = new ArrayList<>();
        list.add("CPPS 110");
        list.add("BIOI 312");


        try {
            user.addCourses(list);
            fail();
        } catch (NoCourseFound noCourseFound) {
            noCourseFound.printClasses();
            System.out.println("It's fine!");
        }
    }

    @Test
    public void testToJsonWithSchedule() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithCourses.json");
        try {
            JSONObject jsonObject = new JSONObject(jsonReader.readFile());
            user.addSectionsFromTimeTable(jsonObject.getJSONObject("Schedule"));

        } catch (Exception e) {
            fail();
        }
        try {
            user.toJson();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSortCourses() {
        List<String> coursesToAdd = new ArrayList<>();
        coursesToAdd.add("BIOL 155"); // TERM 1-2
        coursesToAdd.add("ASIA 100"); // TERM 1
        coursesToAdd.add("BIOL 112"); // TERM 1 AND 2
        coursesToAdd.add("ASIA 101"); // TERM 2

        try {
            user.addCourses(coursesToAdd);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testSortCoursesALot() {
        List<String> coursesToAdd = new ArrayList<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
        } catch (Exception e) {
            fail();
        }

        assertFalse(user.createTimeTable());

    }

    @Test
    public void testALotOfCoursesCreateSchedule() {
        List<String> coursesToAdd = new ArrayList<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
        } catch (Exception e) {
            fail();
        }

        assertFalse(user.createTimeTable());
        assertTrue(user.getFinalTimeTable().size() > 0);
    }
}
