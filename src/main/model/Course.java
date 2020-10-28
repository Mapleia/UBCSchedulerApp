package model;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

// Represents the general course offered by the institution, and has a list of sections.
public class Course implements Comparable<Course> {
    @SerializedName("subject_code")
    private final String subjectCode;
    @SerializedName("course_number")
    private final String courseNum;
    @SerializedName("sections")
    private JsonObject jsonSections;

    private ArrayList<Section> allSection;
    private HashMap<String, Integer> activitySize;
    private HashMap<String, HashMap<String, ArrayList<Section>>> allActivities;
    private int primaryCounter;
    private TimeTable timeTable;

    private boolean hasTerm1 = false;
    private boolean hasTerm2 = false;

    // Constructors
    public Course(String subjectCode, String courseNum, JsonObject jsonSections) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
        this.jsonSections = jsonSections;
    }

    //Constructors
    public Course(String subjectCode, String courseNum) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
    }

    // getters
    public JsonObject getJsonSections() {
        return jsonSections;
    }

    // getters
    public String getSubjectCode() {
        return subjectCode;
    }

    // getters
    public String getCourseNum() {
        return courseNum;
    }

    // getters
    public ArrayList<Section> getAllSection() {
        return allSection;
    }

    // getters
    public HashMap<String, HashMap<String, ArrayList<Section>>> getAllActivities() {
        return allActivities;
    }

    // getters
    public HashMap<String, Integer> getActivitySize() {
        return activitySize;
    }

    // getters
    public int getPrimaryCounter() {
        return primaryCounter;
    }

    // getters
    public boolean isHasTerm1() {
        return hasTerm1;
    }

    // getters
    public boolean isHasTerm2() {
        return hasTerm2;
    }

    // setters
    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
    }



    // MODIFIES: this
    // EFFECT: Create Section Java Object for every section from JSON data. Makes list based on available activities.
    public void addAllSections() {
        initializeArrays();
        ArrayList<String> objKey = new ArrayList<>(jsonSections.keySet());
        Section sec;
        Gson gson = new Gson();
        String status;

        for (String k : objKey) {
            // referenced https://stackoverflow.com/questions/17651395/convert-jsonobject-to-string.
            // referenced https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/.

            String obj = jsonSections.get(k).toString();

            sec = gson.fromJson(obj, Section.class);
            status = sec.getStatus();
            containsTerms(sec);

            if (!status.equalsIgnoreCase("STT")) {
                sec.setTimeTable(timeTable);
                sec.crucialFieldsBlank();
                sec.checkRequired();
                sec.formatDatesAndTime();

                allSection.add(sec);
                mapActivity(sec);
            }
        }
    }

    // MODIFIES: this
    // EFFECT: Initializes arrays because GSON doesn't actually call the constructor...
    private void initializeArrays() {
        allSection = new ArrayList<>();
        activitySize = new HashMap<>();
        allActivities = new HashMap<>();
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
    public void countActivity() {
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
    public void countPrimary() {
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
        return (this.subjectCode == c.subjectCode && this.courseNum == c.courseNum);
    }

    @Override
    public int hashCode() {
        // referenced: https://www.baeldung.com/java-hashcode
        return (int) subjectCode.hashCode() * courseNum.hashCode();
    }
}