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

    public static final String[] ACTIVITIES = { "Discussion", "Lecture", "Web-Oriented Course",
            "Lab", "Seminar", "Tutorial", "Lecture-Lab" };

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

        for (String k : objKey) {
            JsonElement obj = jsonSections.get(k);
            String str = obj.toString();
            sec = gson.fromJson(str, Section.class);
            allSection.add(sec);
        }

        mapActivity();
    }

    // MODIFIES: this
    // EFFECT: Adds to lists divided by activity type and time preference.
    private void mapActivity() throws Exception {
        allActivities = new HashMap<>();

        for (Section sec : allSection) {
            for (String act : ACTIVITIES) {
                if (sec.getActivity().equalsIgnoreCase(act)) {
                    allActivities.put(act, new HashMap<>());
                    allActivities.get(act).putIfAbsent(primaryTimePref, new HashSet<>());
                    allActivities.get(act).putIfAbsent(secondaryTimePref, new HashSet<>());
                    allActivities.get(act).putIfAbsent(tertiaryTimePref, new HashSet<>());

                    String time = sec.getTimeSlot();
                    // If the section's time slot is equal to the primary time preference
                    if (time.equalsIgnoreCase(primaryTimePref)) {
                        allActivities.get(act).get(primaryTimePref).add(sec);
                        break;
                    } else if (time.equalsIgnoreCase(secondaryTimePref)) {
                        allActivities.get(act).get(secondaryTimePref).add(sec);
                        break;
                    } else {
                        allActivities.get(act).get(tertiaryTimePref).add(sec);
                        break;
                    }
                }
            }
        }
        countActivity();

    }

    // MODIFIES: this
    // EFFECT: Counts the number of activities for each available type, and maps it.
    private void countActivity() {
        activitySize = new HashMap<>();

        for (String act : ACTIVITIES) {
            if (allActivities.containsKey(act)) {
                HashMap<String, HashSet<Section>> hash = allActivities.get(act);
                Set<Map.Entry<String, HashSet<Section>>> set = hash.entrySet();

                int counter = 0;
                for (Map.Entry<String, HashSet<Section>> entry : set) {
                    counter += entry.getValue().size();
                }

                activitySize.putIfAbsent(act, counter);
            }

        }
    }
}