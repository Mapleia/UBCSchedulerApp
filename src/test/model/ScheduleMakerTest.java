package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleMakerTest {
        public ScheduleMaker scheduleMaker;
        public TimeTable timeTable;
        public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0);
        timeTable.setTimePref(timeTableTimeArr);

        try {
            timeTable.addCourse("CPSC", "210");
            timeTable.addCourse("MATH", "103");
            timeTable.addCourse("BIOL", "200");
            timeTable.addCourse("BIOL", "140");

        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleMaker = new ScheduleMaker(timeTable);
    }

    @Test
    public void testHasAllCourses() {
        scheduleMaker.makeTimeTable();
        List<Section> finalTable = scheduleMaker.getFinalTimeTable();

        Set<String> courseCodeNames = new HashSet<>();
        for (Course course : timeTable.getCourseList()) {
            courseCodeNames.add(course.getSubjectCode() + " " + course.getCourseNum());
        }

        Set<String> sectionCodeNames = new HashSet<>();
        for (Section sec : finalTable) {
            String sectionCode = sec.getSection().substring(0, sec.getSection().length() - 4);
            sectionCodeNames.add(sectionCode);
        }

        assertTrue(courseCodeNames.containsAll(sectionCodeNames));
    }

}
