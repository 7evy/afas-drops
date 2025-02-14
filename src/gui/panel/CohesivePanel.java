package gui.panel;

import model.DisplayedObject;

import javax.swing.JPanel;
import java.awt.GridLayout;

public abstract class CohesivePanel<T extends DisplayedObject> extends JPanel {
    public CohesivePanel(int rows, int cols) {
        super(new GridLayout(rows, cols, 10, 10));
    }

    public void refresh(T display) {
        removeAll();

        fill(display);
        tieActionListeners(display);

        revalidate();
        repaint();
    }

    protected abstract void fill(T display);

    protected abstract void tieActionListeners(T display);
}
