package persistence;

import model.Course;
import model.ScheduleMaker;
import model.Section;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileFormat {
    private ArrayList<String> courseNamesAndCode;
    private List<Section> finalSchedule;
    private String userName;
    private HashMap<String, String> errorLog;
    private List<String> noTypeDupeList;
    private ScheduleMaker scheduleMakerFile;

    public FileFormat(ScheduleMaker scheduleMaker) {
        this.scheduleMakerFile = scheduleMaker;
        courseNamesAndCode = new ArrayList<>();
        this.createListOfCourses();
        this.finalSchedule = scheduleMakerFile.getFinalTimeTable();
        this.userName = scheduleMakerFile.getUserName();
        this.errorLog = scheduleMakerFile.errorLog;
        this.noTypeDupeList = scheduleMakerFile.noTypeDupeList;
    }

    private void createListOfCourses() {
        for (Course c : scheduleMakerFile.getTimeTable().getCourseList()) {
            courseNamesAndCode.add(c.getSubjectCode() + "-" + c.getCourseNum());
        }
    }

    public JSONObject createObject() {
        JSONObject obj = new JSONObject();
        obj.put("courses", courseNamesAndCode);
        obj.put("schedule", finalSchedule);
        obj.put("username", userName);
        obj.put("error log", errorLog);
        obj.put("noTypeDupeList", noTypeDupeList);

        return obj;
    }

    public String getUserName() {
        return userName;
    }

    public HashMap<String, String> getErrorLog() {
        return errorLog;
    }

    public ArrayList<String> getCourseNamesAndCode() {
        return courseNamesAndCode;
    }

    public List<Section> getFinalSchedule() {
        return finalSchedule;
    }

    public List<String> getNoTypeDupeList() {
        return noTypeDupeList;
    }
}
