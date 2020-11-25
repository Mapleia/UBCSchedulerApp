package ui.gui;

import model.Section;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

// Panel that shows the user their created timetable.
public class SchedulePanel extends JPanel {
    private final SchedulerApp app;
    private HashMap<String, ArrayList<Section>> timetable;

    // constructor
    public SchedulePanel(SchedulerApp app) {
        this.app = app;

        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
        init();
    }

    // initializer, populates the panel
    private void init() {
        timetable = app.getTimeTable();
        add(termPanel("1"));
        add(termPanel("2"));
        add(termPanel("1-2"));
        add(saveFile());

    }

    // REQUIRES: term == "1", "2" or "1-2"
    // EFFECT: prints courses of the given term to the given JTextArea.
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

    // REQUIRES: term == "1", "2" or "1-2"
    //   EFFECT: Creates and returns a panel for each term.
    private JPanel termPanel(String term) {
        JPanel panel = new JPanel();
        JTextArea result = new JTextArea();
        result.setPreferredSize(new Dimension(200, 400));
        result.setBorder(BorderFactory.createLineBorder(Color.black));
        result.setEditable(false);
        printTerm(result, term);

        panel.add(new JLabel("TERM" + term + ": "));
        panel.add(new JScrollPane(result));

        return panel;
    }

    // EFFECT: Creates and returns a panel with the file naming input field for saving a file.
    private JPanel saveFile() {
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
                JOptionPane.showMessageDialog(null,
                        e.getActionCommand() + " was saved successfully.");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null,"An error was found writing your file.");
            }

        });

        panel.add(fileName);
        return panel;
    }
}
