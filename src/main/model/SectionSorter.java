package model;

import java.util.ArrayList;
import java.util.List;

public class SectionSorter {

    // MODIFIES: this
    // EFFECTS: filters sections that is a part of the provided term.
    public static void filterForTerm(Course course, String term) {
        List<String> filteredActivity = new ArrayList<>();
        List<Section> filteredSections = new ArrayList<>();
        for (Section section : course.getSectionList()) {
            if (section.getTerm().equals(term)) {
                if (!filteredActivity.contains(section.getActivity())) {
                    filteredActivity.add(section.getActivity());
                }
                filteredSections.add(section);
            }
        }
        course.setSectionList(filteredSections);
        course.setActivitiesList(filteredActivity);
    }

    // MODIFIES: this
    // EFFECT: sorts sections by time span
    public static void sortSections(Course course) {
        for (String time : course.getPreferences()) {
            course.getSortSections().put(time.toUpperCase(), new ArrayList<>());
        }
        for (Section item : course.getSectionList()) {
            course.getSortSections().get(item.getTimeSpan()).add(item);
        }
    }
}
