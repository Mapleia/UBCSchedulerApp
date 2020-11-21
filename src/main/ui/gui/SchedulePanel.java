package ui.gui;

import model.Section;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class SchedulePanel extends JPanel {
    private final SchedulerApp app;
    private HashMap<String, ArrayList<Section>> timetable;

    public SchedulePanel(SchedulerApp app) {
        this.app = app;

        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
        init();
    }

    private void init() {
        timetable = app.getTimeTable();

        add(termPanel("1"));
        add(termPanel("2"));
        add(termPanel("1-2"));
        add(saveButton());

    }

    // REQUIRES: term == "1", "2" or "1-2"
    private void printTerm(JTextArea result, String term) {
        if (timetable.get(term) == null) {
            result.append("None in Term " + term + ".\n");
            result.append("----------------------------\n");
        } else {
            for (Section sec : timetable.get(term)) {
                result.append("SECTION: " + sec.getSection() + "\n");
                result.append("Type: " + sec.getActivity() + "\n");
                result.append("Start: " + sec.getStartStr() + "\n");
                result.append("End: " + sec.getEndStr() + "\n");
                result.append("Days: " + sec.getDays() + "\n");
                result.append("---------------------------- \n");
            }
        }
    }

    private JPanel termPanel(String term) {
        JPanel panel = new JPanel();
        JTextArea result = new JTextArea();
        result.setPreferredSize(new Dimension(200, 400));
        result.setBorder(BorderFactory.createLineBorder(Color.black));
        result.setEditable(false);
        printTerm(result, term);

        panel.add(new JLabel("TERM" + term + ": "));
        panel.add(result);

        return panel;
    }

    private JPanel saveButton() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter your save file name: "));

        JTextField fileName = new JTextField();
        fileName.setPreferredSize(new Dimension(100, 20));
        fileName.addActionListener(e -> {
            try {
                JsonWriter writer = new JsonWriter(e.getActionCommand());
                writer.open();
                writer.write(app.getUser());
                writer.close();

            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null,"An error was found writing your file.");
            }

        });

        panel.add(fileName);
        return panel;
    }
}
