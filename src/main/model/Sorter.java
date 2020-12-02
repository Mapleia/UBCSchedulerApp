package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sorter {

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

    // EFFECTS: Sort courses into Term1, Term2 or Term1-2 ArrayList hashmaps.
    //          If the course is offered in term1 or 2, it puts the course into term with the least amount of credits.
    public static void sortCourses(User user) {
        HashMap<String, Integer> resultsCredits = new HashMap<>();
        resultsCredits.put("1", 0);
        resultsCredits.put("2", 0);

        for (Course course : user.getCourseSet()) {
            if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("1")) {
                addToTerm1Results(course, resultsCredits, user);

            } else if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("2")) {
                addToTerm2Results(course, resultsCredits, user);

            } else if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("1-2")) {
                Sorter.filterForTerm(course, "1-2");
                Sorter.sortSections(course);
                user.getResult().get("1-2").add(course);

            } else if (resultsCredits.get("2") >= resultsCredits.get("1")) {
                addToTerm1Results(course, resultsCredits, user);

            } else {
                addToTerm2Results(course, resultsCredits, user);
            }
        }
    }

    // HELPER FOR sortCourses
    // EFFECT: Adds Course to Term1, sort course sections according to term specified and removes from the to-do list.
    private static void addToTerm1Results(Course c, HashMap<String, Integer> resultsCredits, User user) {
        Sorter.filterForTerm(c, "1");
        Sorter.sortSections(c);
        user.getResult().get("1").add(c);
        resultsCredits.put("1", resultsCredits.get("1") + c.getCredit());
    }

    // HELPER FOR sortCourses
    // EFFECT: Adds Course to Term2, sort course sections according to term specified and removes from the to-do list.
    private static void addToTerm2Results(Course c, HashMap<String, Integer> resultsCredits, User user) {
        Sorter.filterForTerm(c, "2");
        Sorter.sortSections(c);
        user.getResult().get("2").add(c);
        resultsCredits.put("2", resultsCredits.get("2") + c.getCredit());
    }
}
