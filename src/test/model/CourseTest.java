package model;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    public Course course;
    public Gson gson;
    public File file;
    public Reader readFile;
    @BeforeEach
    public void setup() {
        gson = new Gson();
        file = new File("data\\2020W\\ACAM\\ACAM 250.json");
        try {
            readFile = new FileReader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        course = gson.fromJson(readFile, Course.class);
        try {
            course.setPrimaryTimePref("Afternoon");
            course.setSecondaryTimePrefTimePref("Evening");
            course.setTertiaryTimePrefTimePref("Morning");
            course.addAllSections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCountActivity() {
        assertEquals(1,  course.getActivitySize().get("Web-Oriented Course"));
    }

    @Test
    public void testAddAllSections() {
        assertEquals("ACAM 250 001", course.getAllSection().get(0).getSection());
    }

    @Test
    public void testMapActivity() {
        HashSet<Section> sec = course.getAllActivities().get("Web-Oriented Course").get(course.primaryTimePref);
        assertTrue(sec.contains(course.getAllSection().get(0)));
    }
}