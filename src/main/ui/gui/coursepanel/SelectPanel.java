package ui.gui.coursepanel;

import model.Overview;

import javax.swing.*;

// Panel to allow users to select courses instead of search for them.
public class SelectPanel extends JPanel {
    private final DefaultListModel<String> courses;
    private final DefaultListModel<String> potential;
    private final DefaultListModel<String> courseInDept;
    Overview overview;

    // constructor
    public SelectPanel(Overview overview, DefaultListModel<String> courses) {
        this.overview = overview;
        this.courses = courses;

        potential = new DefaultListModel<>();
        courseInDept = new DefaultListModel<>();
        setBorder(BorderFactory.createTitledBorder("OR Select Course(s) From a Department"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        deptComboBox();
        coursesScrollPane();
        confirmButton();
    }

    //   MODIFIES: this
    //     EFFECT: Create and returns a panel to select department from a drop down menu by adding to the list model.
    // referenced: https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html
    //             for combo box
    private void deptComboBox() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select a Department"));

        JComboBox<String> deptDrownDown = new JComboBox<>(overview.getDepArr().toArray(new String[0]));
        deptDrownDown.addActionListener(e -> {
            courseInDept.removeAllElements();
            if (deptDrownDown.getSelectedItem() != null) {
                for (String item : overview.getCourses(deptDrownDown.getSelectedItem().toString())) {
                    courseInDept.addElement(item);
                }
            }
        });

        panel.add(deptDrownDown);
        add(panel);
    }

    // MODIFIES: this
    //   EFFECT: Creates a scrollable pane where it displays a selectable list of courses from the chosen department.
    private void coursesScrollPane() {
        JList<String> courses = new JList<>(courseInDept);
        courses.setVisibleRowCount(18);
        courses.addListSelectionListener(e -> {
            potential.removeAllElements();
            for (String item : courses.getSelectedValuesList()) {
                potential.addElement(item);
            }
        });
        JScrollPane courseFromDept = new JScrollPane(courses);

        add(courseFromDept);
    }

    // MODIFIES: this
    //   EFFECT: Create and returns a button that confirms to add courses.
    private void confirmButton() {
        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(e -> {
            for (int i = 0; i < potential.size(); i++) {
                if (!courses.contains(potential.elementAt(i))) {
                    courses.addElement(potential.elementAt(i));
                }
            }
        });

        add(confirmBtn);
    }
}
