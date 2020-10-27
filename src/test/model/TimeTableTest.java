package model;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTableTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};
    public Gson gson;
    public File file;
    public Reader readFile;

    @BeforeEach
    public void setup() {
        gson = new Gson();

        timeTable = new TimeTable(2020, 0);
        timeTable.setTimePref(timeTableTimeArr);
    }

    @Test
    public void testAddCourseValid() {
        try {
            timeTable.addCourse("CPSC", "210");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        file = new File("data\\2020W\\CPSC\\CPSC 210.json");
        try {
            readFile = new FileReader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Course cpscCourse = gson.fromJson(readFile, Course.class);
        cpscCourse.setTimeTable(timeTable);
        cpscCourse.addAllSections();
        cpscCourse.countActivity();

        Course courseFromTT = timeTable.getCourseList().get(0);
        assertEquals(cpscCourse.getCourseNum(), courseFromTT.getCourseNum());
        assertEquals(cpscCourse.getSubjectCode(), courseFromTT.getSubjectCode());
        assertEquals(cpscCourse.getAllSection().get(0).getSection(),
                        courseFromTT.getAllSection().get(0).getSection());
    }

    @Test
    public void testSetTimePref() {
        timeTable.setTimePref(timeTableTimeArr);
        assertEquals(timeTableTimeArr[0], timeTable.primaryTimePref);
        assertEquals(timeTableTimeArr[1], timeTable.secondaryTimePref);
        assertEquals(timeTableTimeArr[2], timeTable.tertiaryTimePref);
    }

    @Test
    public void testAddCourseException() {
        try {
            timeTable.addCourse("CPSS", "210");
            fail();
        } catch (Exception e){
            System.out.println("No course found.");
        }
    }

    @Test
    public void testSetters() {
        timeTable.setSpreadClasses(true);
        assertTrue(timeTable.getSpreadClasses());
    }

}
