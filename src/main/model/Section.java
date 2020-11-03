package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Section implements Writable {
    private JSONObject jsonObject;
    private String status;
    private String section;
    private String activity;
    private String term;
    private List<String> days;
    private String start;
    private String end;

    // constructor for section.
    public Section(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        init();
    }

    private void init() {
        days = new ArrayList<>();
        createSection();
    }

    // EFFECT: serializes section object to a JSONObject.
    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("section", section);
        obj.put("activity", activity);
        obj.put("term", term);
        obj.put("days", days);
        obj.put("start", start);
        obj.put("end", end);

        return obj;
    }

    // getter for section
    public String getSection() {
        return section;
    }

    // getter for start
    public String getStart() {
        return start;
    }

    // getter for days
    public List<String> getDays() {
        return days;
    }

    // EFFECTS: parses section from JSON object.
    private void createSection() {
        status = jsonObject.getString("status");
        section = jsonObject.getString("section");
        activity = jsonObject.getString("activity");
        term = jsonObject.getString("term");

        for (int i = 0; i < jsonObject.getJSONArray("days").length(); i++) {
            days.add(jsonObject.getJSONArray("days").getString(i));
        }

        start = jsonObject.getString("start");
        end = jsonObject.getString("end");

    }
}
