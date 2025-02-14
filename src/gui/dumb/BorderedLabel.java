package gui.dumb;

import javax.swing.*;
import java.awt.*;

public class BorderedLabel extends BorderedPanel {
    public final JLabel inner;
    
    public BorderedLabel(String label) {
        super(0, 0);
        inner = new JLabel(label, JLabel.CENTER);
        add(inner, BorderLayout.CENTER);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(200, 40);
    }
}
