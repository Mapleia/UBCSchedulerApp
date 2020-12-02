package model;

import java.util.*;

// Represents a course at UBC.
public class Course implements Comparable<Course> {
    private final String courseName;
    private final List<String> termsList;
    private List<String> activitiesList;
    private List<Section> sectionList;
    private final int credit;
    private final List<String> preferences;

    private HashMap<String, Section> sectionsMap;
    private HashMap<String, ArrayList<Section>> sortSections;

    // constructor for course
    public Course(String courseName, List<String> termsList, List<String> activitiesList, List<Section> sectionList,
                  int credit, List<String> preferences)  {
        this.courseName = courseName;
        this.termsList = termsList;
        this.activitiesList = activitiesList;
        this.sectionList = sectionList;
        this.credit = credit;
        this.preferences = new LinkedList<>(preferences);
        init();
    }

    // EFFECTS: initializes all of the fields, adds, then sorts sections.
    private void init()  {
        preferences.add("N/A");
        sectionsMap = new HashMap<>();
        mapSections();
        sortSections = new HashMap<>();
    }

    // getters & setter ================================================================================================
    public List<String> getTerms() {
        return termsList;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public void setActivitiesList(List<String> activitiesList) {
        this.activitiesList = activitiesList;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public HashMap<String, Section> getSectionsMap() {
        return sectionsMap;
    }

    public int getCredit() {
        return credit;
    }

    public HashMap<String, ArrayList<Section>> getSortSections() {
        return sortSections;
    }

    public List<String> getActivitiesList() {
        return activitiesList;
    }

    public String getCourseName() {
        return courseName;
    }
    // ================================================================================================================

    // MODIFIES: this
    // EFFECTS: parses sections from JSON object and adds them to the course.
    private void mapSections() {
        for (Section sec : sectionList) {
            if (sec.isRequired() && !activitiesList.contains("Required")) {
                activitiesList.add("Required");
            }
            sectionsMap.put(sec.getSection(), sec);
        }
    }

    @Override
    public int hashCode() {
        return 31 * courseName.hashCode() * sectionsMap.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Course other = (Course) obj;
        return courseName.equals(other.getCourseName()) && sectionsMap.size() == other.getSectionsMap().size();
    }

    @Override
    public int compareTo(Course o) {
        int sizeMe = sectionsMap.size();
        int sizeThem = o.getSectionsMap().size();

        return Integer.compare(sizeMe, sizeThem);
    }
}
