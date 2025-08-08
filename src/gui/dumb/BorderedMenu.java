package gui.dumb;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BorderedMenu extends BorderedPanel {
    public final JMenu inner;

    public BorderedMenu(String label) {
        super(60, 0);
        pad();
        inner = new JMenu(label);
        add(inner, BorderLayout.CENTER);
    }

    public void add(JCheckBoxMenuItem checkBox) {
        inner.add(checkBox);
    }

    public List<JCheckBoxMenuItem> getCheckBoxes() {
        return Arrays.stream(inner.getMenuComponents())
                .filter(c -> c instanceof JCheckBoxMenuItem)
                .map(c -> (JCheckBoxMenuItem) c)
                .toList();
    }
}
