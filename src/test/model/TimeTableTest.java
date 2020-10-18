package model;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeTableTest {
    public TimeTable timeTable;
    public Course cpscCourse;

    public Gson gson;
    public File file;
    public Reader readFile;

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable();

        gson = new Gson();
        file = new File("data\\2020W\\CPSC\\CPSC 210.json");
        try {
            readFile = new FileReader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpscCourse = gson.fromJson(readFile, Course.class);
        try {
            cpscCourse.addAllSections();
            cpscCourse.setPrimaryTimePref("Afternoon");
            cpscCourse.setSecondaryTimePrefTimePref("Evening");
            cpscCourse.setTertiaryTimePrefTimePref("Morning");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddCourseValid() {
        try {
            timeTable.addCourse("CPSC", "210");
        }
        catch (Exception e) {
            System.out.println("Cannot find course.");
            e.printStackTrace();
        }

        Course courseFromTT = timeTable.getCourseList().get(0);
        assertEquals(cpscCourse.getCourseNum(), courseFromTT.getCourseNum());
        assertEquals(cpscCourse.getSubjectCode(), courseFromTT.getSubjectCode());
        assertEquals(cpscCourse.getAllSection().get(0).getSection(),
                        courseFromTT.getAllSection().get(0).getSection());
    }
}
