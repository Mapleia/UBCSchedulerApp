package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

public class OverlapChecker {

    // EFFECT: Returns true if the section overlaps with any in the collection.
    public static boolean isOverlapping(Section section, Collection<Section> list) {
        for (Section item : list) {
            if (isOverlapping(section, item)) {
                return true;
            }
        }
        return false;
    }

    // EFFECT: Returns true if the sections are overlapping.
    private static boolean isOverlapping(Section section1, Section section2) {
        if (section1.getStart() == null || section2.getStart() == null) {
            return false;
        } else if (section1.getEnd() == null || section2.getEnd() == null) {
            return false;
        } else if (section1.getFirstWeekList().size() > section2.getFirstWeekList().size()) {
            return isOverlappingHelper(section1, section2);
        } else {
            return isOverlappingHelper(section2, section1);
        }
    }

    // REQUIRES: section1 to have more days then section2, start & end cannot be null.
    //   EFFECT: Returns true if sections are overlapping.
    private static boolean isOverlappingHelper(Section section1, Section section2) {
        boolean result = false;
        for (LocalDate date : section1.getFirstWeekList()) {
            for (LocalDate date2 : section2.getFirstWeekList()) {
                LocalTime start1 = section1.getStart();
                LocalTime start2 = section2.getStart();
                LocalTime end1 = section1.getEnd();
                LocalTime end2 = section2.getEnd();

                if (LocalDateTime.of(date, start1).isEqual(LocalDateTime.of(date2, start2))
                        || LocalDateTime.of(date, end1).isEqual(LocalDateTime.of(date2, end2))) {
                    result = true;
                } else {
                    boolean s1IsBeforeS2 = LocalDateTime.of(date, start1).isBefore(LocalDateTime.of(date2, end2));
                    boolean s2IsBeforeE1 = LocalDateTime.of(date2, start2).isBefore(LocalDateTime.of(date, end1));
                    if (s1IsBeforeS2 && s2IsBeforeE1) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
