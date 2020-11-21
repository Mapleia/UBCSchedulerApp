package model;

import exceptions.NoCourseFound;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.util.*;

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
        Set<String> courses = new HashSet<>();
        courses.add("CPSC 210");
        courses.add("BIOL 112");
        try {
            user.addCourses(courses);
            user.createTimeTable();
            user.setYear("2020W");
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

        assertEquals("BIOL 112 102",
                user.getFinalTimeTable().get("1").get(0).getSection());
        assertEquals("BIOL 112 T04",
                user.getFinalTimeTable().get("1").get(1).getSection());
        assertEquals("CPSC 210 202",
                user.getFinalTimeTable().get("2").get(0).getSection());
        assertEquals("CPSC 210 L2B",
                user.getFinalTimeTable().get("2").get(1).getSection());
    }

    @Test
    public void testAddCoursesException() {
        List<String> list = new ArrayList<>();
        list.add("CPPS 110");
        list.add("BIOI 312");


        try {
            user.addCourses(new HashSet<>(list));
            fail();
        } catch (NoCourseFound noCourseFound) {
            noCourseFound.printClasses();
            noCourseFound.size();
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
    public void testClearFinalTable() {
        Set<String> coursesToAdd = new HashSet<>();
        coursesToAdd.add("BIOL 155"); // TERM 1-2
        coursesToAdd.add("ASIA 100"); // TERM 1
        coursesToAdd.add("BIOL 112"); // TERM 1 AND 2
        coursesToAdd.add("ASIA 101"); // TERM 2

        try {
            user.addCourses(coursesToAdd);
            user.createTimeTable();
            user.clearTimetable();
        } catch (Exception e) {
            fail();
        }
        assertEquals(new HashMap<String, ArrayList<Section>>(), user.getFinalTimeTable());
    }

    @Test
    public void testALotOfCoursesSchedule() {
        Set<String> coursesToAdd = new HashSet<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");
        coursesToAdd.add("CPSC 310");
        coursesToAdd.add("BIOL 234");
        coursesToAdd.add("JAPN 200");
        coursesToAdd.add("CHEM 235");
        coursesToAdd.add("MATH 103");
        coursesToAdd.add("STAT 200");
        coursesToAdd.add("PHYS 100");
        coursesToAdd.add("WRDS 150B");
        coursesToAdd.add("MICB 201");
        coursesToAdd.add("CPSC 320");
        coursesToAdd.add("CPSC 213");
        coursesToAdd.add("CPSC 410");
        coursesToAdd.add("BIOL 260");

        try {
            user.addCourses(coursesToAdd);
            user.createTimeTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(user.getErrorLog().size() >0);
    }

    @Test
    public void testStandardCreateSchedule() {
        Set<String> coursesToAdd = new HashSet<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
            user.createTimeTable();
        }  catch (NoCourseFound n) {
            n.printClasses();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //TODO: tests for removeCourses, both successful and unsuccessful.

    @Test
    public void removeCourses() {
        Set<String> coursesToAdd = new HashSet<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
            user.removeCourses(Arrays.asList(new String[]{"BIOL 155", "BIOL 200"}));
            user.createTimeTable();
        }  catch (NoCourseFound n) {
            n.printClasses();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void removeCoursesFail() {
        Set<String> coursesToAdd = new HashSet<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
            user.removeCourses(Arrays.asList(new String[]{"BIOL 140", "BIOL 200"}));
            user.createTimeTable();
            fail();
        }  catch (NoCourseFound n) {
            n.printClasses();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
