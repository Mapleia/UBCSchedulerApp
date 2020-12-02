package ui.gui.coursepanel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

// EFFECT: Creates panel to input time preference.
public class TimePrefPanel extends JPanel {

    // constructor
    public TimePrefPanel(DefaultListModel<String> timePrefModel) {
        // https://www.tutorialspoint.com/how-to-set-vertical-alignment-for-a-component-in-java
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Time Preference"));

        String instructions = "Please click and drag to order your time preference.";
        JList<String> orderTimePref = new JList<>(timePrefModel);
        orderTimePref.setDragEnabled(true);
        orderTimePref.setDropMode(DropMode.INSERT);
        orderTimePref.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTimePref.setTransferHandler(new DragAndDrop(orderTimePref, timePrefModel));
        orderTimePref.setBorder(BorderFactory.createEtchedBorder());
        orderTimePref.setFixedCellHeight(30);
        orderTimePref.setFixedCellWidth(100);

        add(makeWrapLabelText(instructions));
        add(orderTimePref);
    }

    //     EFFECT: Create label looking like text that can wrap around.
    // referenced: https://stackoverflow.com/questions/26420428/how-to-word-wrap-text-in-jlabel
    public JTextArea makeWrapLabelText(String labelText) {
        JTextArea text = new JTextArea(2, 20);
        text.append(labelText);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setOpaque(false);
        text.setEditable(false);
        text.setFocusable(false);
        text.setBackground(UIManager.getColor("Label.background"));
        text.setFont(UIManager.getFont("Label.font"));
        text.setBorder(UIManager.getBorder("Label.border"));

        return text;
    }

    // handles list items being transferred around
    // copied from https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop
    private class DragAndDrop extends TransferHandler {
        private final JList<String> list;
        private final DefaultListModel<String> model;
        private int index;
        private boolean beforeIndex = false;
        //Start with `false` therefore if it is removed from or added to the list it still works

        public DragAndDrop(JList<String> list, DefaultListModel<String> model) {
            super();
            this.list = list;
            this.model = model;
        }

        @Override
        public int getSourceActions(JComponent comp) {
            return MOVE;
        }

        @Override
        public Transferable createTransferable(JComponent comp) {
            index = list.getSelectedIndex();
            return new StringSelection(list.getSelectedValue());
        }

        @Override
        public void exportDone(JComponent comp, Transferable trans, int action) {
            if (action == MOVE) {
                if (beforeIndex) {
                    model.remove(index + 1);
                } else {
                    model.remove(index);
                }
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            try {
                String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                model.add(dl.getIndex(), s);
                beforeIndex = dl.getIndex() < index;
                return true;
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
