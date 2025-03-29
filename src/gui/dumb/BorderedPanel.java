package gui.dumb;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class BorderedPanel extends JPanel {
    public BorderedPanel(int horizontalBorder, int verticalBorder) {
        super(new BorderLayout(horizontalBorder, verticalBorder));
    }

    public void pad() {
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.EAST);
    }

    public void padHorizontal() {
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.EAST);
    }

    public void padVertical() {
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.SOUTH);
    }

    public void no(String constraint) {
        remove(((BorderLayout) getLayout()).getLayoutComponent(constraint));
    }
}
