package ui.gui.coursepanel;

import model.Overview;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.stream.Collectors;

// EFFECT: Creates and returns panel to search for and select courses.
public class SearchPanel extends JPanel {
    private final DefaultListModel<String> courseListModel;
    private JTextArea searchSuggestion;
    private String searchTerm = "";
    private List<String> deptArr;
    private final Overview overview;

    public SearchPanel(Overview overview, DefaultListModel<String> courseListModel) {
        this.overview = overview;
        this.courseListModel = courseListModel;

        init();
    }

    private void init() {
        searchSuggestion = new JTextArea(20, 10);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        searchBarPanel();
        updateSearchSuggestionPanel();
        add(new JScrollPane(searchSuggestion));

    }

    // EFFECT: Creates a search bar.
    private void searchBarPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Search For A Course"));
        panel.add(new JLabel("Search (PRESS ENTER)"));

        JTextField field = new JTextField();
        field.setColumns(10);
        // listener, updates the "searchTerm" value as it updates
        field.getDocument().addDocumentListener(new SearchBarListener());
        // listener, if enter is pressed, and only 1 course is displayed in the suggested course list, add to model
        field.addKeyListener(new SearchBarKeyListener());
        panel.add(field);

        add(panel);
    }

    // EFFECT: Updates and populates supplied JTextArea with courses or department, depending on the searched course.
    public void updateSearchSuggestionPanel() {
        StringBuilder fieldStr = new StringBuilder();
        deptArr = createOverviewList();
        if (deptArr != null) {
            for (String dept : deptArr) {
                fieldStr.append(dept).append("\n");
            }
        }
        searchSuggestion.setText(fieldStr.toString());
    }

    // EFFECT: Creates a list of departments or courses depending on the search term input.
    private List<String> createOverviewList() {
        if (searchTerm.contains(" ")) {
            int i = searchTerm.indexOf(" ");
            String dept = searchTerm.substring(0, i);
            return overview.getCourses(dept).stream().filter(s -> s.contains(searchTerm)).collect(Collectors.toList());
        } else {
            return overview.getDepArr().stream().filter(s -> s.contains(searchTerm)).collect(Collectors.toList());
        }
    }

    private class SearchBarListener implements DocumentListener {
        public SearchBarListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                setValue(e);

            } catch (BadLocationException badLocationException) {
                System.out.println("Contents: Unknown");
                searchTerm = "";
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            try {
                setValue(e);

            } catch (BadLocationException badLocationException) {
                System.out.println("Contents: Unknown");
                searchTerm = "";
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void setValue(DocumentEvent e) throws BadLocationException {
            int length = e.getDocument().getLength();
            searchTerm = e.getDocument().getText(0, length).toUpperCase();
            updateSearchSuggestionPanel();
        }
    }

    private class SearchBarKeyListener implements KeyListener {
        // constructor
        public SearchBarKeyListener() {
        }

        // EFFECT: If key is typed, trigger this.
        @Override
        public void keyTyped(KeyEvent e) {

        }

        // EFFECT: If key (enter) is pressed, add course.
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                if (deptArr != null && deptArr.size() == 1) {
                    if (!courseListModel.contains(deptArr.get(0))) {
                        courseListModel.addElement(deptArr.get(0));
                    }
                }
            }
        }

        // EFFECT: If key is released, trigger this.
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
