package model;

import java.util.*;
import java.util.stream.Collectors;

// Represents the object that manipulates TimeTable to create the final product (list of sections).
public class ScheduleMaker {
    private final TimeTable timeTable;
    private final List<Section> finalTimeTable;
    public final HashMap<String, String> errorLog = new HashMap<>();
    private final List<String> noTypeDupeList = new ArrayList<>();
    private final ArrayList<Course> allCourses;

    // constructor
    public ScheduleMaker(TimeTable timeTable) {
        this.timeTable = timeTable;
        allCourses = timeTable.getCourseList();
        finalTimeTable = new ArrayList<>();
    }

    // getters
    public List<Section> getFinalTimeTable() {
        return finalTimeTable;
    }

    // getters
    public HashMap<String, String> getErrorLog() {
        return errorLog;
    }

    // getters
    public ArrayList<Course> getAllCourses() {
        return allCourses;
    }

    // REQUIRES: Course list size > 0.
    // EFFECT: Create a valid timetable per term.
    public void makeTimeTable() {
        Collections.sort(allCourses);

        // idea from: https://stackoverflow.com/questions/9146224/arraylist-filter

        List<Course> allCoursesT1 = allCourses.stream().filter(c -> c.isHasTerm1() && !c.isHasTerm2())
                .collect(Collectors.toList());
        List<Course> allCoursesT2 = allCourses.stream().filter(c -> !c.isHasTerm1() && c.isHasTerm2())
                .collect(Collectors.toList());
        List<Course> allCoursesT1T2 = allCourses.stream().filter(c -> c.isHasTerm1() && c.isHasTerm2())
                .collect(Collectors.toList());

        makeTimeTablePerTerm(allCoursesT1);
        makeTimeTablePerTerm(allCoursesT2);
        makeTimeTablePerTerm(allCoursesT1T2);
    }

    // REQUIRES: list of valid courses.
    // MODIFIES: this.
    // EFFECT: For every course in the list, add the first section (in the time that is the most desired)
    // that fits into the schedule.
    private void makeTimeTablePerTerm(List<Course> courseList) {
        for (Course course : courseList) {
            Set<String> keySet = course.getAllActivities().keySet();
            ActivityLoop: {
                for (String key : keySet) {
                    List<Section> sectionList = course.getAllActivities().get(key).get(timeTable.primaryTimePref);

                    if (addValidSectionForActivity(sectionList)) {
                        sectionList = course.getAllActivities().get(key).get(timeTable.secondaryTimePref);

                        if (addValidSectionForActivity(sectionList)) {
                            sectionList = course.getAllActivities().get(key).get(timeTable.tertiaryTimePref);

                            if (addValidSectionForActivity(sectionList)) {
                                errorLog.put(course.getSubjectCode() + "-" + course.getCourseNum(), key);
                                break ActivityLoop;
                            }
                        }
                    }
                }
            }

        }

    }

    // EFFECT: returns true if 1 valid section could not be added from the list given. Adds to list otherwise.
    private boolean addValidSectionForActivity(List<Section> sections) {
        boolean didNotAddSection = true;

        for (Section section : sections) {
            if (finalTimeTable.isEmpty()) {
                successfulAddSection(section);
                didNotAddSection = false;
                break;
            } else if (isTypeDupes(section)) {
                didNotAddSection = false;
                break;
            } else if (section.isCrucialFieldsBlank()) {
                successfulAddSection(section);
                didNotAddSection = false;
                break;
            } else if (!section.isOverlapping(finalTimeTable)) {
                successfulAddSection(section);
                didNotAddSection = false;
                break;
            }
        }

        return didNotAddSection;
    }

    // MODIFIES: this
    // EFFECT: Adds to final list of sections that will be the timetable. Adds to list of already done course
    // activity types so no dupes are added.
    private void successfulAddSection(Section sec) {
        finalTimeTable.add(sec);
        noTypeDupeList.add(sec.getSection().substring(0, sec.getSection().length() - 4) + "-" + sec.getActivity());
    }

    // EFFECT: Returns true if there are duplicate types (Web-Oriented Course, Lectures and Waiting List).
    private boolean isTypeDupes(Section sec) {
        boolean result = false;
        String sectionCode = sec.getSection().substring(0, sec.getSection().length() - 4);
        if (noTypeDupeList.contains(sectionCode + "-" + "Web-Oriented Course")) {
            if (sec.getActivity().equals("Lecture")) {
                result = true;
            }
        } else if (noTypeDupeList.contains(sectionCode + "-" + "Lecture")) {
            if (sec.getActivity().equals("Waiting List")) {
                result = true;
            }
        }

        return result;
    }
}