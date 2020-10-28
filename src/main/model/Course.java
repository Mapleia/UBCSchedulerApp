package model;

import java.util.*;

import org.json.JSONObject;
import persistence.JsonReader;

// Represents the general course offered by the institution, and has a list of sections.
public class Course implements Comparable<Course> {
    private final String subjectCode;
    private final String courseNum;

    private ArrayList<Section> allSection;
    private HashMap<String, Integer> activitySize;
    private HashMap<String, HashMap<String, ArrayList<Section>>> allActivities;
    private int primaryCounter;
    private TimeTable timeTable;

    private boolean hasTerm1 = false;
    private boolean hasTerm2 = false;

    // Constructors
    public Course(String subjectCode, String courseNum, JSONObject jsonSections, TimeTable timeTable) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
        this.timeTable = timeTable;

        allActivities = new HashMap<>();
        addAllSections(jsonSections);
        countActivity();
        countPrimary();
    }

    //Constructors
    public Course(String subjectCode, String courseNum) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
    }

    // getters
    public String getSubjectCode() {
        return subjectCode;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public ArrayList<Section> getAllSection() {
        return allSection;
    }

    public HashMap<String, HashMap<String, ArrayList<Section>>> getAllActivities() {
        return allActivities;
    }

    public HashMap<String, Integer> getActivitySize() {
        return activitySize;
    }

    public int getPrimaryCounter() {
        return primaryCounter;
    }

    public boolean isHasTerm1() {
        return hasTerm1;
    }

    public boolean isHasTerm2() {
        return hasTerm2;
    }

    // MODIFIES: this
    // EFFECT: Create Section Java Object for every section from JSON data. Makes list based on available activities.
    private void addAllSections(JSONObject jsonSections) {
        allSection = new ArrayList<>();

        for (int i = 0; i < jsonSections.names().length(); i++) {
            JSONObject section = jsonSections.getJSONObject(jsonSections.names().getString(i));
            Section sec = parseFromJsonObject(section);
            String status = sec.getStatus();
            containsTerms(sec);

            if (!status.equalsIgnoreCase("STT")) {
                allSection.add(sec);
                mapActivity(sec);
            }
        }
    }

    private Section parseFromJsonObject(JSONObject obj) {
        String status = obj.getString("status");
        String section = obj.getString("section");
        String activity = obj.getString("activity");
        String term = obj.getString("term");
        String days = obj.getString("days");
        String start = obj.getString("start");
        String end = obj.getString("end");

        return new Section(status, section, start, end, activity, term, days, timeTable);
        // String status, String section, String start, String end, String activity, String term, String days,
        //                   TimeTable timeTable
    }

    // MODIFIES: this
    // EFFECT: sets fields to true (hasTerm1 & hasTerm2) if the term is 1 or 2 or both.
    private void containsTerms(Section section) {
        if (section.getTerm().trim().contains("1")) {
            hasTerm1 = true;
        }
        if (section.getTerm().trim().contains("2")) {
            hasTerm2 = true;
        }
    }

    // MODIFIES: this
    // EFFECT: Adds to lists divided by activity type and time preference.
    private void mapActivity(Section section) {

        String time;
        if (section.isCrucialFieldsBlank()) {
            time = timeTable.primaryTimePref;
        } else {
            time = section.getTimeSlot();
        }

        String activityType = section.getActivity();

        HashMap<String, ArrayList<Section>> setOfActivity = new HashMap<>();
        setOfActivity.putIfAbsent(timeTable.primaryTimePref, new ArrayList<>());
        setOfActivity.putIfAbsent(timeTable.secondaryTimePref, new ArrayList<>());
        setOfActivity.putIfAbsent(timeTable.tertiaryTimePref, new ArrayList<>());

        allActivities.putIfAbsent(activityType, setOfActivity);

        if (time.equalsIgnoreCase(timeTable.primaryTimePref)) {
            allActivities.get(activityType).get(timeTable.primaryTimePref).add(section);

        } else if (time.equalsIgnoreCase(timeTable.secondaryTimePref)) {
            allActivities.get(activityType).get(timeTable.secondaryTimePref).add(section);

        } else {
            allActivities.get(activityType).get(timeTable.tertiaryTimePref).add(section);
        }
    }

    // MODIFIES: this
    // EFFECT: Counts the number of activities for each available type, and maps it.
    private void countActivity() {
        activitySize = new HashMap<>();

        Set<String> allKeys = allActivities.keySet();
        String[] timePrefs = {timeTable.primaryTimePref, timeTable.secondaryTimePref, timeTable.tertiaryTimePref};
        /*
         * (Activity1: [Primary: (Section1, Section2, Section3),
         *              Secondary: (Section4, Section5),
         *              Tertiary: (EMPTY)],
         *  Activity2: [Primary: (Section6, Section7),
         *              Secondary: (Section8),
         *              Tertiary: ()] )
         */
        // referenced https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap.
        for (String act : allKeys) {
            int counter = 0;
            for (String s : timePrefs) {
                counter += allActivities.get(act).get(s).size();
            }

            activitySize.put(act, counter);
        }
    }

    // REQUIRES: allActivities is already mapped.
    // MODIFIES: this
    // EFFECT: Count number of primary time-slotted sections.
    private void countPrimary() {
        Set<String> allKeys = allActivities.keySet();

        int counter = 0;
        for (String act : allKeys) {
            counter += allActivities.get(act).get(timeTable.primaryTimePref).size();
        }

        primaryCounter = counter;
    }

    // REQUIRES: Another valid course.
    // EFFECT: A compareTo used to sort list of courses.
    @Override
    public int compareTo(Course o) {
        return Integer.compare(primaryCounter, o.getPrimaryCounter());
    }

    @Override
    public boolean equals(Object course) {
        // referenced: https://stackoverflow.com/questions/24957813/indexof-will-not-find-a-custom-object-type
        if (!(course instanceof Course)) {
            return false;
        }
        Course c = (Course) course;
        return (this.subjectCode.equals(c.subjectCode) && this.courseNum.equals(c.courseNum));
    }

    @Override
    public int hashCode() {
        // referenced: https://www.baeldung.com/java-hashcode
        return subjectCode.hashCode() * courseNum.hashCode();
    }
}