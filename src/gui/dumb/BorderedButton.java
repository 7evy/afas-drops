package gui.dumb;

import javax.swing.JButton;
import java.awt.BorderLayout;

public class BorderedButton extends BorderedPanel {
    public final JButton inner;

    public BorderedButton(String label, int border) {
        super(border, 0);
        pad();
        inner = new JButton(label);
        add(inner, BorderLayout.CENTER);
    }

    public BorderedButton(String label) {
        this(label, 10);
    }

    public void addActionListener(Runnable onActionPerformed) {
        this.inner.addActionListener(e -> onActionPerformed.run());
    }
}
