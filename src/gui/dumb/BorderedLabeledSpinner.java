package gui.dumb;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

public class BorderedLabeledSpinner extends BorderedPanel {
    public final JSpinner inner;

    public BorderedLabeledSpinner(String label, SpinnerModel model) {
        super(70, 0);
        padVertical();
        JPanel subPanel = new JPanel(new GridLayout(1, 2));
        inner = new JSpinner(model);
        JLabel jLabel = new JLabel(label, JLabel.CENTER);
        subPanel.add(jLabel);
        subPanel.add(inner);
        add(subPanel, BorderLayout.CENTER);
    }

    public void addChangeListener(Runnable onActionPerformed, boolean highlightChange) {
        inner.addChangeListener(e -> {
            onActionPerformed.run();
            if (highlightChange) inner.getEditor().getComponent(0).setBackground(Color.YELLOW);
        });
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(150, 45);
    }
}
