package gui.dumb;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

public class BorderedLabeledComboBox<T> extends BorderedPanel {
    private final JComboBox<T> inner;

    public BorderedLabeledComboBox(String label, T[] items, T selected) {
        super(0, 0);
        JPanel subPanel = new JPanel(new GridLayout(1, 2));
        inner = new JComboBox<T>(items);
        inner.setSelectedItem(selected);
        JLabel jLabel = new JLabel(label, JLabel.CENTER);
        subPanel.add(jLabel);
        subPanel.add(inner);
        add(subPanel, BorderLayout.CENTER);
    }

    public void addActionListener(Runnable onActionPerformed, boolean highlightChange) {
        inner.addActionListener(ignored -> {
            onActionPerformed.run();
            if (highlightChange) inner.setBackground(new Color(200, 200, 0));
        });
    }

    @SuppressWarnings("unchecked")
    public T getSelectedItem() {
        return (T) inner.getSelectedItem();
    }

    public void setSelectedItem(T item) {
        inner.setSelectedItem(item);
    }

    public void addItem(T item) {
        inner.addItem(item);
    }

    public void removeAllItems() {
        inner.removeAllItems();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(200, 45);
    }
}
