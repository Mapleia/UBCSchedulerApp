package model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OverlapCheckerTest {
    @Test
    public void testIsOverlappingType1() {
        // [====]
        //    [====]
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end,
                "2020W");

        LocalTime start2 = LocalTime.of(9, 30, 0);
        LocalTime end2 = LocalTime.of(11, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1-2", Collections.singletonList("TUE"), start2, end2,
                "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));

        List<Section> listToCheck2 = new ArrayList<>();
        listToCheck2.add(comparison1);
        assertTrue(OverlapChecker.isOverlapping(comparison2, listToCheck2));

    }

    @Test
    public void testIsOverlappingType2() {
        //    [====]
        // [====]
        LocalTime start = LocalTime.of(9, 30, 0);
        LocalTime end = LocalTime.of(11, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end, "2020W");

        LocalTime start2 = LocalTime.of(9, 0, 0);
        LocalTime end2 = LocalTime.of(10, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        assertTrue(OverlapChecker.isOverlapping(comparison1, Collections.singleton(comparison2)));

    }

    @Test
    public void testIsOverlappingType3() {
        //    [====]
        // [===========]
        LocalTime start = LocalTime.of(9, 30, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end, "2020W");

        LocalTime start2 = LocalTime.of(8, 30, 0);
        LocalTime end2 = LocalTime.of(11, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));
    }

    @Test
    public void testIsOverlappingType4() {
        // [====]
        // [====]
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end, "2020W");

        LocalTime start2 = LocalTime.of(9, 0, 0);
        LocalTime end2 = LocalTime.of(10, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));
    }

    @Test
    public void testIsOverlappingType5() {
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end, "2020W");

        // [====]
        // [==========]
        LocalTime start2 = LocalTime.of(9, 0, 0);
        LocalTime end2 = LocalTime.of(11, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));

        // [====]
        // [==]
        LocalTime start3 = LocalTime.of(9, 0, 0);
        LocalTime end3 = LocalTime.of(9, 30, 0);
        Section comparison3 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start3, end3, "2020W");
        listToCheck = new ArrayList<>();
        listToCheck.add(comparison3);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));
    }

    @Test
    public void testIsOverlappingType6() {
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU"), start, end, "2020W");

        //       [====]
        // [==========]
        LocalTime start2 = LocalTime.of(8, 0, 0);
        LocalTime end2 = LocalTime.of(10, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));

        // [====]
        //   [==]
        LocalTime start3 = LocalTime.of(9, 30, 0);
        LocalTime end3 = LocalTime.of(10, 0, 0);
        Section comparison3 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start3, end3, "2020W");
        listToCheck = new ArrayList<>();
        listToCheck.add(comparison3);
        assertTrue(OverlapChecker.isOverlapping(comparison1, listToCheck));
    }

    @Test
    public void testIsOverlappingTypeNull() {
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU", "SAT"), start, end, "2020W");


        LocalTime end2 = LocalTime.of(10, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), null, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertFalse(OverlapChecker.isOverlapping(comparison1, listToCheck));

        assertFalse(OverlapChecker.isOverlapping(comparison2, Collections.singleton(comparison1)));


        LocalTime start3 = LocalTime.of(9, 30, 0);
        Section comparison3 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start3, null, "2020W");
        assertFalse(OverlapChecker.isOverlapping(comparison1, Collections.singleton(comparison3)));
        assertFalse(OverlapChecker.isOverlapping(comparison3, Collections.singleton(comparison1)));

        Section comparison4 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", new ArrayList<>(), start3, end2, "2020W");
        assertFalse(OverlapChecker.isOverlapping(comparison1, Collections.singleton(comparison4)));

    }

    @Test
    public void testIsOverlappingTypeOutOfBounds() {
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(9, 50, 0);
        Section comparison1 = new Section(" ", "BIOL 200 001", "BIOL 200",
                "Web-Oriented Course", "1", Arrays.asList("TUE", "THU", "SAT"), start, end, "2020W");

        LocalTime start2 = LocalTime.of(10, 0, 0);

        LocalTime end2 = LocalTime.of(11, 0, 0);
        Section comparison2 = new Section(" ", "CPSC 110 001", "CPSC 110",
                "Web-Oriented Course", "1", Collections.singletonList("TUE"), start2, end2, "2020W");

        List<Section> listToCheck = new ArrayList<>();
        listToCheck.add(comparison2);
        assertFalse(OverlapChecker.isOverlapping(comparison1, listToCheck));
        listToCheck = new ArrayList<>();
        listToCheck.add(comparison1);

        assertFalse(OverlapChecker.isOverlapping(comparison2, listToCheck));

    }
}
