package gui;

import javax.swing.JButton;
import java.awt.BorderLayout;

public class BorderedButton extends BorderedPanel {
    public final JButton inner;

    public BorderedButton(String label) {
        super(10, 10);
        inner = new JButton(label);
        add(inner, BorderLayout.CENTER);
    }

    public void addActionListener(Runnable onActionPerformed) {
        this.inner.addActionListener(e -> onActionPerformed.run());
    }
}
