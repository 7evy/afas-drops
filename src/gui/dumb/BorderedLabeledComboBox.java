package gui.dumb;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

public class BorderedLabeledComboBox extends BorderedPanel {
    public final JComboBox<String> inner;

    public BorderedLabeledComboBox(String label, List<String> items, String selected) {
        super(0, 0);
        JPanel subPanel = new JPanel(new GridLayout(1, 2));
        inner = new JComboBox<String>(items.toArray(String[]::new));
        inner.setSelectedItem(selected);
        JLabel jLabel = new JLabel(label, JLabel.CENTER);
        subPanel.add(jLabel);
        subPanel.add(inner);
        add(subPanel, BorderLayout.CENTER);
    }

    public void addActionListener(Runnable onActionPerformed, boolean highlightChange) {
        inner.addActionListener(e -> {
            onActionPerformed.run();
            if (highlightChange) inner.setBackground(new Color(200, 200, 0));
        });
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(200, 45);
    }
}
