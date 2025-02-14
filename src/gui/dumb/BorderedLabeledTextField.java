package gui.dumb;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class BorderedLabeledTextField extends BorderedPanel {
    public final JTextField inner;

    public BorderedLabeledTextField(String label, String value, boolean editable) {
        this(label);
        inner.setText(value);
        inner.setEditable(editable);
    }

    public BorderedLabeledTextField(String label) {
        super(50, 0);
        JPanel subPanel = new JPanel(new GridLayout(1, 2));
        inner = new JTextField();
        JLabel jLabel = new JLabel(label, JLabel.CENTER);
        subPanel.add(jLabel);
        subPanel.add(inner);
        add(subPanel, BorderLayout.CENTER);
    }

    public void addActionListener(Runnable onActionPerformed) {
        inner.addActionListener(e -> {
            onActionPerformed.run();
            inner.setBackground(Color.YELLOW);
        });
        inner.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}
            @Override
            public void focusLost(FocusEvent e) {
                onActionPerformed.run();
                inner.setBackground(Color.YELLOW);
            }
        });
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(400, 45);
    }
}
