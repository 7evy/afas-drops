package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class BorderedPanel extends JPanel {
    public BorderedPanel(int horizontalBorder, int verticalBorder) {
        super(new BorderLayout(horizontalBorder, verticalBorder));
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
    }

    public void no(String constraint) {
        remove(((BorderLayout) getLayout()).getLayoutComponent(constraint));
    }
}
