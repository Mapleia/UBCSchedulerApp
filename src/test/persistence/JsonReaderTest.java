package persistence;

import model.Course;
import model.Section;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {
    private JsonReader jsonReader;
    public List<String> preferences = new LinkedList<>();

    @BeforeEach
    public void setup() {
        jsonReader = new JsonReader("./data/noSuchFile.json");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");
    }

    @Test
    public void testNonExistentFileUser() {
        try {
            User user = jsonReader.readUser();

            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testNonExistentFileCourse() {
        try {
            Course course = jsonReader.readCourse("2020W", preferences);

            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyUser() {
        JsonReader reader = new JsonReader("./data/timetables/testWriterEmptyUser.json");
        try {
            User user = reader.readUser();
            assertEquals(user.getFinalTimeTable(), new HashMap<>());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderNoStart() {
        JsonReader reader = new JsonReader("./data/2020W/JAPN/JAPN 200.json");
        try {
            Course course = reader.readCourse("2020W", Arrays.asList("MORNING", "AFTERNOON", "EVENING"));
            course.sortSections();
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testWrongSection() {
        JsonReader reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        try {
            Section section = reader.readSection("2020W", "CPSC 210 000",
                    Arrays.asList("MORNING", "AFTERNOON", "EVENING", "N/A"));
            fail();
        } catch (IOException e) {
            System.out.println("It's fine!");
        }
    }

    @Test
    void testReadSectionWrongCourse() {
        JsonReader reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        try {
            Section section = reader.readSection("2020W", "CPPC 210 000",
                    Arrays.asList("MORNING", "AFTERNOON", "EVENING", "N/A"));
            fail();
        } catch (IOException e) {
            System.out.println("It's fine!");
        }
    }

    @Test
    void testReadSectionTrue() {
        JsonReader reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        try {
            Section section = reader.readSection("2020W", "CPSC 210 L1A",
                    Arrays.asList("MORNING", "AFTERNOON", "EVENING", "N/A"));
        } catch (IOException e) {
            fail();
        }
    }
}
