package ui.gui;

import model.Section;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

// Panel that shows the user their created timetable.
public class SchedulePanel extends JPanel {
    private final SchedulerApp app;
    private HashMap<String, HashSet<Section>> timetable;

    // constructor
    public SchedulePanel(SchedulerApp app) {
        this.app = app;

        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
        init();
    }

    // initializer, populates the panel
    private void init() {
        timetable = app.getTimeTable();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(termPanel("1"));
        panel.add(termPanel("2"));
        panel.add(termPanel("1-2"));
        add(saveFile());
        add(panel);
        add(errorLogPanel());
        add(app.backButton(new CoursePanel(app)));
        validate();

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
        result.setEditable(false);

        JScrollPane pane = new JScrollPane(result);
        pane.setPreferredSize(new Dimension(200, 400));
        pane.setBorder(BorderFactory.createLineBorder(Color.black));
        printTerm(result, term);

        panel.add(new JLabel("TERM" + term + ": "));
        panel.add(pane);

        return panel;
    }

    // EFFECT: Creates and returns a panel with the file naming input field for saving a file.
    private JPanel saveFile() {
        JPanel panel = new JPanel();

        JTextArea fileName = new JTextArea();
        fileName.setPreferredSize(new Dimension(100, 20));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                JsonWriter writer = new JsonWriter(fileName.getText());
                writer.open();
                writer.write(app.getUser());
                writer.close();
                JOptionPane.showMessageDialog(null,
                        e.getActionCommand() + " was saved successfully.");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null,"An error was found writing your file.");
            }
        });

        panel.add(new JLabel("Enter your save file name: "));
        panel.add(fileName);
        panel.add(saveButton);
        return panel;
    }

    // EFFECT: Create and returns a panel that displays the section type that could not be added.
    private JPanel errorLogPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Error Log"));

        if (!app.getUser().getErrorLog().isEmpty()) {
            JTextArea log = new JTextArea();
            log.setBorder(BorderFactory.createEtchedBorder());

            for (String item : app.getUser().getErrorLog()) {
                log.append(item + "\n");
            }

            panel.add(log);
        }

        return panel;
    }
}
