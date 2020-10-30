package persistence;

import model.ScheduleMaker;
import model.Section;
import model.TimeTable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileFormat {
    private final Set<String> courses;
    private final List<Section> finalSchedule;
    private final String userName;
    private final HashMap<String, String> errorLog;
    private final TimeTable timeTable;

    public FileFormat(ScheduleMaker scheduleMaker) {
        courses = scheduleMaker.getAllCourses().keySet();
        this.finalSchedule = scheduleMaker.getFinalTimeTable();
        this.timeTable = scheduleMaker.getTimeTable();
        this.userName = scheduleMaker.getUserName();
        this.errorLog = scheduleMaker.errorLog;
    }

    public JSONObject createObject() {
        JSONObject obj = new JSONObject();
        obj.put("courses", courses);
        obj.put("schedule", finalSchedule);
        obj.put("username", userName);
        obj.put("error log", errorLog);

        return obj;
    }

    public String getUserName() {
        return userName;
    }

    public HashMap<String, String> getErrorLog() {
        return errorLog;
    }

    public Set<String> getCourses() {
        return courses;
    }

    public List<Section> getFinalSchedule() {
        return finalSchedule;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

}
