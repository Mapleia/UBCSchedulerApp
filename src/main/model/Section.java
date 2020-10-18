package model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Section {
    private final String section;
    @SerializedName("start")
    protected String startStr;
    @SerializedName("end")
    protected String endStr;
    protected final String activity;
    private final String term;
    private final String days;

    private Date startD;
    private Date endD;
    private static final ArrayList<Date> startTimes = new ArrayList<>();
    // private static final ArrayList<Date> endTimes = new ArrayList<>();
    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

    // constructor
    public Section(String section, String start, String end, String activity, String term, String days) {
        this.section = section;
        this.startStr = start;
        this.endStr = end;
        this.activity = activity;
        this.term = term;
        this.days = days;
    }

    // getters
    public String getSection() {
        return section;
    }

    // getters
    public String getActivity() {
        return activity;
    }

    // returns if the
    public String getTimeSlot() throws Exception {
        formatTime();
        String time = "";

        for (int i = 0; i < 3; i++) {
            // used to be startD.after(startTimes.get(i)) && endD.before(endTimes.get(i))
            if (startD.after(startTimes.get(i))) {
                if (i == 0) {
                    time = "Morning";
                } else if (i == 1) {
                    time = "Afternoon";
                } else {
                    time = "Evening";
                }
            }
        }

        return time;
    }

    // REQUIRES: startStr and endStr to be in a 24hr HH:mm format
    // MODIFIES: this
    // EFFECT: Format time to parse the strings into Date object
    private void formatTime() throws ParseException {
        addTimesToList();

        int startInt = Integer.parseInt(startStr.substring(0, 2));
        if (startStr.length() > 2) {
            if (startInt == 12) {
                startStr += ":00 PM";
            }
            if (startInt > 12) {
                startInt -= 12;
                startStr = startInt + startStr.substring(2) + "PM";
            }

        }

        int endInt = Integer.parseInt(endStr.substring(0, 2));
        if (endStr.length() > 2) {
            if (endInt == 12) {
                endStr += ":00 PM";
            }
            if (endInt > 12) {
                endInt -= 12;
                endStr = endInt + endStr.substring(2) + ":00 PM";
            }
        }

        startD = dateFormat.parse(startStr);
        endD = dateFormat.parse(endStr);
    }

    // MODIFIES: this
    // EFFECT: Add Date objects with the boundary start and end times to a list.
    private void addTimesToList() throws ParseException {
        startTimes.add(dateFormat.parse("07:59:99 AM"));
        startTimes.add(dateFormat.parse("11:59:99 AM"));
        startTimes.add(dateFormat.parse("4:59:99 PM"));

        //endTimes.add(dateFormat.parse("12:00:01 PM"));
        //endTimes.add(dateFormat.parse("5:00:01 PM"));
        //endTimes.add(dateFormat.parse("9:00:01 PM"));
    }
}
