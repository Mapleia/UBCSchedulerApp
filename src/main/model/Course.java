package model;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Course {
    @SerializedName("subject_code")
    private final String subjectCode;
    @SerializedName("course_number")
    private final String courseNum;
    @SerializedName("sections")
    private final JsonObject jsonSections;

    private ArrayList<Section> allSection;
    private HashMap<String, Integer> activitySize;
    private HashMap<String, HashMap<String, HashSet<Section>>> allActivities;

    public String primaryTimePref;
    public String secondaryTimePref;
    public String tertiaryTimePref;

    // Constructors
    public Course(String subjectCode, String courseNum, JsonObject jsonSections) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
        this.jsonSections = jsonSections;
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
    public HashMap<String, HashMap<String, HashSet<Section>>> getAllActivities() {
        return allActivities;
    }

    // getters
    public HashMap<String, Integer> getActivitySize() {
        return activitySize;
    }

    // setters
    public void setPrimaryTimePref(String time) {
        this.primaryTimePref = time;
    }

    // setters
    public void setSecondaryTimePrefTimePref(String time) {
        this.secondaryTimePref = time;
    }

    // setters
    public void setTertiaryTimePrefTimePref(String time) {
        this.tertiaryTimePref = time;
    }

    // MODIFIES: this
    // EFFECT: Create Section Java Object for every section from JSON data. Makes list based on available activities.
    public void addAllSections() throws Exception {
        ArrayList<String> objKey = new ArrayList<>(jsonSections.keySet());
        Section sec;
        Gson gson = new Gson();
        allSection = new ArrayList<>();
        allActivities = new HashMap<>();

        String status;

        for (String k : objKey) {
            // referenced https://stackoverflow.com/questions/17651395/convert-jsonobject-to-string.
            // referenced https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/.

            JsonElement obj = jsonSections.get(k);
            String str = obj.toString();
            sec = gson.fromJson(str, Section.class);

            status = sec.getStatus();
            if (!status.equalsIgnoreCase("STT")) {
                allSection.add(sec);
                mapActivity(sec);

            }
        }

        countActivity();
    }

    // MODIFIES: this
    // EFFECT: Adds to lists divided by activity type and time preference.
    private void mapActivity(Section section) throws Exception {
        String time = section.getTimeSlot();
        String activityType = section.getActivity();

        HashMap<String, HashSet<Section>> setOfActivity = new HashMap<>();
        setOfActivity.putIfAbsent(primaryTimePref, new HashSet());
        setOfActivity.putIfAbsent(secondaryTimePref, new HashSet());
        setOfActivity.putIfAbsent(tertiaryTimePref, new HashSet());

        allActivities.putIfAbsent(activityType, setOfActivity);

        if (time.equalsIgnoreCase(primaryTimePref)) {
            allActivities.get(activityType).get(primaryTimePref).add(section);

        } else if (time.equalsIgnoreCase(secondaryTimePref)) {
            allActivities.get(activityType).get(secondaryTimePref).add(section);

        } else {
            allActivities.get(activityType).get(tertiaryTimePref).add(section);
        }

        countActivity();

    }

    // MODIFIES: this
    // EFFECT: Counts the number of activities for each available type, and maps it.
    private void countActivity() {
        activitySize = new HashMap<>();
        Set<String> allKeys = allActivities.keySet();

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
            HashMap<String, HashSet<Section>> hash = allActivities.get(act);
            Set<Map.Entry<String, HashSet<Section>>> set = hash.entrySet();

            int counter = 0;
            for (Map.Entry<String, HashSet<Section>> entry : set) {
                counter += entry.getValue().size();
            }

            activitySize.put(act, counter);
        }
    }
}