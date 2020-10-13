package model;


import java.util.ArrayList;

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

    private boolean hasDiscussion = false;
    private boolean hasLecture = false;
    private boolean hasWebCourse = false;
    private boolean hasLab = false;
    private boolean hasSeminar = false;
    private boolean hasTutorial = false;
    private boolean hasLectureLab = false;


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

    // getter
    public boolean isHasDiscussion() {
        return hasDiscussion;
    }

    // getters
    public boolean isHasLecture() {
        return hasLecture;
    }

    // getters
    public boolean isHasWebCourse() {
        return hasWebCourse;
    }

    // getters
    public boolean isHasLab() {
        return hasLab;
    }

    // getters
    public boolean isHasSeminar() {
        return hasSeminar;
    }

    // getters
    public boolean isHasLectureLab() {
        return hasLectureLab;
    }

    public boolean isHasTutorial() {
        return hasTutorial;
    }


    public void addAllSections() {
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

        checkAvailableActivity();
    }

    private void checkAvailableActivity() {
        for (Section s : allSection) {
            if (s.getActivity().equalsIgnoreCase("Discussion")) {
                hasDiscussion = true;
            } else if (s.getActivity().equalsIgnoreCase("Lecture")) {
                hasLecture = true;
            } else if (s.getActivity().equalsIgnoreCase("Web-Oriented Course")) {
                hasWebCourse = true;
            } else if (s.getActivity().equalsIgnoreCase("Lab")) {
                hasLab = true;
            }  else if (s.getActivity().equalsIgnoreCase("Seminar")) {
                hasSeminar = true;
            } else if (s.getActivity().equalsIgnoreCase("Tutorial")) {
                hasTutorial = true;
            } else if (s.getActivity().equalsIgnoreCase("Lecture-Lab")) {
                hasLectureLab = true;
            }
        }
    }

    // REQUIRES: Time in 24:00 clock, in PST timezone.
    // EFFECT: Given a start and end time, checks if it has any sections available in that time.
    public ArrayList<Section> hasAvailableTimeSection() {
        return allSection;
    }
}
