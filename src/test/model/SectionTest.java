package model;


import exceptions.NoCourseFound;
import exceptions.NoSectionFound;
import exceptions.NoTimeSpanAdded;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    private static final String[] PREFERENCE_ARRAY = {"Afternoon", "Morning", "Evening"};
    private TimeTable timeTableW;
    private Course cpsc210;
    private Section sectionL1U;
    private Section sectionT;
    private final String[] cpsc210L1U = new String[]{"CPSC 210 L1U", "Laboratory"};


    @BeforeEach
    public void setup() {
        timeTableW = new TimeTable(2020, true, PREFERENCE_ARRAY);

        try {
            cpsc210 = Course.createCourse("CPSC", "210", timeTableW);
            JSONObject obj = JsonReader.findCourseFile("CPSC", "210", timeTableW);

            sectionL1U = cpsc210.get(cpsc210L1U[0]);
            sectionT = Section.createSection(obj.getJSONObject("sections").getJSONObject("L1U"), timeTableW);

        } catch (NoCourseFound e) {
            fail("No course found, setup failed.");
            e.printStackTrace();
        } catch (JSONException e) {
            fail("Failed to get JSONObject.");
            e.printStackTrace();
        } catch (NoSectionFound n) {
            fail("Fail to find section.");
            n.printStackTrace();
        } catch (NoTimeSpanAdded t) {
            fail("Fail to make timespan, no term.");
            t.printStackTrace();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testConstructor() {
        assertEquals("Full", sectionL1U.getStatus());
        assertEquals(cpsc210L1U[0], sectionL1U.getSection());
        assertEquals("18:00", sectionL1U.getStart());
        assertEquals("20:00", sectionL1U.getEnd());
        assertEquals(cpsc210L1U[1], sectionL1U.getActivity());
        assertEquals("1", sectionL1U.getTerm());
        assertEquals(" Tue", sectionL1U.getDays());
        assertFalse(sectionL1U.isCrucialFieldsBlank());
        assertEquals("Evening", sectionL1U.getTimeSlot());
    }

    @Test
    public void testEquals() {
        assertTrue(sectionL1U.equals(sectionT));
        try {
            assertFalse(sectionT.equals(cpsc210.get("CPSC 210 101")));
        } catch (NoSectionFound noSectionFound) {
            noSectionFound.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEqualsNotSection() {
        assertFalse(sectionL1U.equals(cpsc210));
    }

    @Test
    public void testHashCode() {
        assertEquals(sectionL1U.hashCode(), sectionT.hashCode());
    }

    @Test
    public void testOverlappingTrueFull() {
        // s ----- e
        // 18 --- 20
        // s ----- e
        Section sectionNew = new Section("Full", "BIOL 200 LI8", "18:00", "20:00",
                "Laboratory", "1", "Tue", timeTableW);
        assertTrue(sectionNew.isOverlapping(sectionL1U));
    }

    @Test
    public void testOverlappingTrueStart() {
        // OG:    s ----- e
        //        18 --- 20
        //  N: s ----- e
        //     17 --- 19
        Section sectionNew = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Laboratory", "1", "Tue", timeTableW);
        assertTrue(sectionNew.isOverlapping(sectionL1U));
    }

    @Test
    public void testOverlappingTrueLong() {
        // OG:  s ----- e
        //      18 --- 20
        // N: s -------- e
        //    17 ------ 20:30
        Section sectionNew = new Section("Full", "BIOL 200 LI8", "17:00", "20:30",
                "Laboratory", "1", "Tue", timeTableW);
        assertTrue(sectionNew.isOverlapping(sectionL1U));
    }

    @Test
    public void testOverlappingTrueEnd() {
        // OG:  s ----- e
        //      18 --- 20
        // N:       s ----- e
        //          17 --- 20:30
        Section sectionNew = new Section("Full", "BIOL 200 LI8", "19:30", "21:30",
                "Laboratory", "1", "Tue", timeTableW);
        assertTrue(sectionNew.isOverlapping(sectionL1U));
    }

    @Test
    public void testOverlappingMultipleTrue() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Laboratory", "1", "Tue", timeTableW);
        Section s2 = new Section("Full", "BIOL 200 LI8", "17:00", "20:30",
                "Laboratory", "1", "Mon", timeTableW);
        Section s3 = new Section("Full", "BIOL 200 LI8", "19:30", "21:30",
                "Laboratory", "1", "Wed", timeTableW);

        List<Section> listS = new ArrayList<>();
        listS.add(s1);
        listS.add(s2);
        listS.add(s3);
        assertTrue(sectionL1U.isOverlapping(listS));
    }

    @Test
    public void testOverlappingEmpty() {
        List<Section> listS = new ArrayList<>();
        assertFalse(sectionL1U.isOverlapping(listS));
    }

    @Test
    public void testOverlappingMultipleFalse() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Laboratory", "1", "Fri", timeTableW);
        Section s2 = new Section("Full", "BIOL 200 LI8", "17:00", "20:30",
                "Laboratory", "1", "Mon", timeTableW);
        Section s3 = new Section("Full", "BIOL 200 LI8", "19:30", "21:30",
                "Laboratory", "1", "Wed", timeTableW);
        List<Section> listS = new ArrayList<>();
        listS.add(s1);
        listS.add(s2);
        listS.add(s3);
        assertFalse(sectionL1U.isOverlapping(listS));
    }

    @Test
    public void testOverlappingTrueMultipleDays() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Laboratory", "1", "Tue Thu", timeTableW);
        assertTrue(sectionL1U.isOverlapping(s1));
        assertTrue(s1.isOverlapping(sectionL1U));

    }



    @Test
    public void testDaysIsBlankNotLecture() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Laboratory", "1", " ", timeTableW);
        assertEquals("Laboratory", s1.getActivity());
    }

    @Test
    public void testDaysIsBlankLecture() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Lecture", "1", " ", timeTableW);
        assertEquals("Required", s1.getActivity());
    }

    @Test
    public void testDaysIsBlankWebOrientedCourse() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Web-Oriented Course", "1", " ", timeTableW);
        assertEquals("Required", s1.getActivity());
    }

    @Test
    public void testFormatDateAndTime() {
        TimeSpan zdt = sectionL1U.getTimeSpans().get(0);

        ZonedDateTime startT;
        ZonedDateTime endT;

        startT = ZonedDateTime.of(2020, 9, 8, 18, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        endT = ZonedDateTime.of(2020, 9, 8, 20, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        assertEquals(startT, zdt.getStart());
        assertEquals(endT, zdt.getEnd());
    }

    @Test
    public void testFormatDateAndTimeSpring() {

        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Web-Oriented Course", "2", "Tue", timeTableW);
        TimeSpan zdt = s1.getTimeSpans().get(0);

        ZonedDateTime startT;
        ZonedDateTime endT;

        startT = ZonedDateTime.of(2021, 1, 5, 17, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        endT = ZonedDateTime.of(2021, 1, 5, 19, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        assertEquals(startT, zdt.getStart());
        assertEquals(endT, zdt.getEnd());
    }

    @Test
    public void testFormatDateAndTimeSummer1() {
        TimeTable timeTableS = new TimeTable(2021, false, PREFERENCE_ARRAY);

        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Web-Oriented Course", "1", "Tue", timeTableS);

        TimeSpan zdt = s1.getTimeSpans().get(0);

        ZonedDateTime startT;
        ZonedDateTime endT;

        startT = ZonedDateTime.of(2021, 5, 4, 17, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        endT = ZonedDateTime.of(2021, 5, 4, 19, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        assertEquals(startT, zdt.getStart());
        assertEquals(endT, zdt.getEnd());
    }

    @Test
    public void testFormatDateAndTimeSummer2() {
        TimeTable timeTableS = new TimeTable(2021, false, PREFERENCE_ARRAY);

        Section s1 = new Section("Full", "BIOL 200 LI8", "17:00", "19:00",
                "Web-Oriented Course", "2", "Tue", timeTableS);

        TimeSpan zdt = s1.getTimeSpans().get(0);

        ZonedDateTime startT;
        ZonedDateTime endT;

        startT = ZonedDateTime.of(2021, 7, 6, 17, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        endT = ZonedDateTime.of(2021, 7, 6, 19, 0, 0, 0,
                ZoneId.of(TimeSpan.TIMEZONE));

        assertEquals(startT, zdt.getStart());
        assertEquals(endT, zdt.getEnd());
    }

    @Test
    public void testEmptyTimeSpans() {

        Section s1 = new Section("Full", "BIOL 200 LI8", " ", " ",
                "Web-Oriented Course", "2", " ", timeTableW);

        assertTrue(s1.getTimeSlot().equals(""));
        assertTrue(s1.getTimeSpans().isEmpty());
    }

    @Test
    public void testNoTerm() {
        Section s1 = new Section("Full", "BIOL 200 LI8", "18:00", "19:00",
                "Web-Oriented Course", " ", "Tue", timeTableW);
        assertEquals(9, s1.getTimeSpans().get(0).getStart().getMonthValue());
    }

    @Test
    public void testOverlappingDoubleTerm() {
        try {
            Section s1 = new Section("Full", "BIOL 200 LI8", "14:30", "17:00",
                    "Web-Oriented Course", "2", "Tue", timeTableW);
            Course biol155 = Course.createCourse("BIOL", "155", timeTableW);
            Section s2 = biol155.get("BIOL 155 001");
            assertTrue(
                    TimeSpan.isOverlapping(s1.getTimeSpans().get(0), s2.getTimeSpans().get(2))
                || TimeSpan.isOverlapping(s2.getTimeSpans().get(2), s1.getTimeSpans().get(0)));
            assertTrue(s1.isOverlapping(s2));
        } catch (NoTimeSpanAdded noTimeSpanAdded) {
            noTimeSpanAdded.printTerm();
            fail();
        } catch (NoCourseFound n) {
            n.printCourse();
            fail();
        } catch (NoSectionFound | IOException noSectionFound) {
            noSectionFound.printStackTrace();
        }
    }

    @Test
    public void testSummerT12() {
        try {
            TimeTable tt = new TimeTable(2021, false, PREFERENCE_ARRAY);
            Section section = Course.createCourse("BIOL", "155", tt).get("BIOL 155 001");
            assertEquals(5, section.getTimeSpans().get(0).getStart().getMonthValue());
            assertEquals(7, section.getTimeSpans().get(2).getStart().getMonthValue());

        } catch (Exception e) {
            fail();
        }
    }
}
