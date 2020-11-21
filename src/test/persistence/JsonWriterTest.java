package persistence;

import model.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    @Test
    public void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/timetables/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testWriterEmptyUser() {
        try {
            User user = new User("2020W");
            user.setPreferences(Arrays.asList("AFTERNOON", "MORNING", "EVENING", "N/A"));
            JsonWriter writer = new JsonWriter("testWriterEmptyUser");
            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testWriterEmptyUser.json");
            user = reader.readUser();
            assertEquals(user.getFinalTimeTable(), new HashMap<>());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    public void testWriterGeneralUser() {
        User user = new User("2020W");
        try {
            Set<String> courses = new HashSet<>(Arrays.asList(new String[]{"CPSC 210", "CPSC 110", "BIOL 112"}));
            user.setPreferences(Arrays.asList("AFTERNOON", "EVENING", "MORNING"));
            user.addCourses(courses);
            user.createTimeTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            JsonWriter writer = new JsonWriter("testUserWithSections");

            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testUserWithSections.json");
            user = reader.readUser();
            assertEquals("CPSC 110 101",
                    user.getFinalTimeTable().get("1").get(0).getSection());
            assertEquals("CPSC 110 L12",
                    user.getFinalTimeTable().get("1").get(1).getSection());
            assertEquals("CPSC 110 101",
                    user.getFinalTimeTable().get("1").get(0).getSection());
            assertEquals("CPSC 210 L2A",
                    user.getFinalTimeTable().get("2").get(1).getSection());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception should not have been thrown");
        }
    }

    @Test
    public void testLotsOfCoursesUser() {// this user is crazy
        User user = new User("2020W");
        List<String> preferences = new LinkedList<>();
        preferences.add("Afternoon".toUpperCase());
        preferences.add("Evening".toUpperCase());
        preferences.add("Morning".toUpperCase());
        user.setPreferences(preferences);

        List<String> coursesToAdd = new ArrayList<>();
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
            user.addCourses(new HashSet<>(coursesToAdd));
            user.createTimeTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            JsonWriter writer = new JsonWriter("testUserWithLotOfCourse");

            writer.open();
            writer.write(user);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception should not have been thrown");
        }
    }
}
