package ui.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class SearchBarListener implements DocumentListener {
    private final CoursePanel panel;
    private String value;

    public SearchBarListener(CoursePanel panel) {
        this.panel = panel;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            setValue(e);

        } catch (BadLocationException badLocationException) {
            System.out.println("Contents: Unknown");
            value = "Contents: Unknown";
        }
    }

    private void setValue(DocumentEvent e) throws BadLocationException {
        int length = e.getDocument().getLength();
        value = e.getDocument().getText(0, length);
        panel.setSearchValue(value);
        panel.updateSearchSuggestionPanel();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            setValue(e);

        } catch (BadLocationException badLocationException) {
            System.out.println("Contents: Unknown");
            value = "Contents: Unknown";
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
