package ui.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

// referenced: https://stackoverflow.com/questions/15169674/jtextfield-with-both-actionlistener-and-documentlistener
// for keyPressed method
public class SearchBarKeyListener implements KeyListener {
    private final CoursePanel panel;

    // constructor
    public SearchBarKeyListener(CoursePanel panel) {
        this.panel = panel;
    }

    // EFFECT: If key is typed, trigger this.
    @Override
    public void keyTyped(KeyEvent e) {

    }

    // EFFECT: If key (enter) is pressed, add course.
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            List<String> deptArr = panel.getDeptArr();
            if (deptArr != null && deptArr.size() == 1) {
                panel.addCourseList(deptArr.get(0));
                if (!panel.modelContains(deptArr.get(0))) {
                    panel.addToCourseModel(deptArr.get(0));
                }
                panel.printCourses();
            }
        }
    }

    // EFFECT: If key is released, trigger this.
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
