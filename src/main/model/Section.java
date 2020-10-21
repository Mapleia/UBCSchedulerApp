package model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Section {
    private final String status;
    private final String section;
    private final String activity;
    private final String term;
    private final String days;

    private Date startD;
    private Date endD;


    @SerializedName("start")
    private String startStr;
    @SerializedName("end")
    private final String endStr;

    private static final ArrayList<Date> startTimes = new ArrayList<>();
    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

    // constructor
    public Section(String status, String section, String start, String end, String activity, String term, String days) {
        this.status = status;
        this.section = section;
        this.startStr = start;
        this.endStr = end;
        this.activity = activity;
        this.term = term;
        this.days = days;
    }

    // getters
    public String getStatus() {
        return status;
    }

    // getters
    public String getSection() {
        return section;
    }

    // getters
    public String getActivity() {
        return activity;
    }

    // getters
    public String getTerm() {
        return term;
    }

    // getters
    public String getDays() {
        return days;
    }

    // getters
    public Date getStartD() {
        return startD;
    }

    // getters
    public Date getEndD() {
        return endD;
    }

    // REQUIRES: startStr and endStr to not be null
    // MODIFIES: this
    // EFFECT: Returns the section's time slot (morning / afternoon / evening).
    // and assigns value in Date to startD and endD.
    public String getTimeSlot() throws ParseException {
        startD = formatTime(startStr);
        endD = formatTime(endStr);

        String timeSlot = "";
        for (int i = 0; i < 3; i++) {
            // used to be startD.after(startTimes.get(i)) && endD.before(endTimes.get(i))
            if (startD.after(startTimes.get(i))) {
                if (i == 0) {
                    timeSlot = "Morning";
                }
                if (i == 1) {
                    timeSlot = "Afternoon";
                }
                if (i == 2) {
                    timeSlot = "Evening";
                }
            }
        }
        return timeSlot;
    }

    // REQUIRES: Time string to be in a 24hr HH:mm format
    // MODIFIES: this
    // EFFECT: Format time to parse the strings into Date object
    private Date formatTime(String timeStr) throws ParseException {
        addTimesToList();
        if (timeStr.length() < 5) {
            timeStr = "0" + timeStr + ":00 AM";
        } else {
            int hourInt = Integer.parseInt(timeStr.substring(0, 2));
            if (hourInt > 12) {
                if ((hourInt - 12) == 0) {
                    timeStr += ":00 PM";

                } else {
                    if ((hourInt - 12) < 10) {
                        timeStr = (hourInt - 12) + timeStr.substring(2) + ":00 PM";

                    } else {
                        timeStr = "0" + (hourInt - 12) + timeStr.substring(2) + ":00 PM";
                    }
                }
            } else {
                timeStr = timeStr + ":00 AM";

            }
        }
        return dateFormat.parse(timeStr);

    }

    // MODIFIES: this
    // EFFECT: Add Date objects with the boundary start and end times to a list.
    private void addTimesToList() throws ParseException {
        startTimes.add(dateFormat.parse("07:59:99 AM"));
        startTimes.add(dateFormat.parse("11:59:99 AM"));
        startTimes.add(dateFormat.parse("04:59:99 PM"));
    }
}
