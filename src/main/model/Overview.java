package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Overview {

    private ArrayList<String> depArr;
    private HashMap<String, ArrayList<String>> courseMap;

    public Overview(ArrayList<String> depArr, HashMap<String, ArrayList<String>> courseMap) {
        this.depArr = depArr;
        this.courseMap = courseMap;
    }

    public ArrayList<String> getDepArr() {
        return depArr;
    }

    public ArrayList<String> getCourses(String dept) {
        return courseMap.get(dept);
    }

}
