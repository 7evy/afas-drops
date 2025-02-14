package gui.frame;

import gui.panel.CohesivePanel;
import gui.dumb.BorderedButton;
import gui.dumb.BorderedPanel;
import model.DisplayedObject;
import model.FEObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class WritableItemFrame<T extends FEObject> extends JFrame {

    protected final DefaultListModel<String> listModel = new DefaultListModel<>();
    protected final JList<String> list;

    protected T object;

    public WritableItemFrame(String title, String label, int width, int height) {
        super(title);
        setPreferredSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        BorderedButton newTemplateButton = new BorderedButton("New " + label);
        newTemplateButton.addActionListener(this::newTemplate);

        BorderedButton saveButton = new BorderedButton("Write to memory");
        saveButton.addActionListener(this::save);

        BorderedButton refreshButton = new BorderedButton("Refresh");
        refreshButton.addActionListener(this::refreshPanel);

        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);
        subLayout.no(BorderLayout.EAST);
        subLayout.no(BorderLayout.WEST);

        BorderedPanel buttonContainer = new BorderedPanel(100, 0);
        buttonContainer.no(BorderLayout.NORTH);
        JPanel subButtonContainer = new JPanel(new GridLayout(1, 3));
        subButtonContainer.add(newTemplateButton);
        subButtonContainer.add(saveButton);
        subButtonContainer.add(refreshButton);
        buttonContainer.add(subButtonContainer, BorderLayout.CENTER);

        refreshListModel();
        list = new JList<>(listModel);
        list.setSelectedIndex(0);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshPanel();
            }
        });
        subLayout.add(list, BorderLayout.WEST);
        subLayout.add(buttonContainer, BorderLayout.NORTH);

        subLayout.add(getPanel(), BorderLayout.CENTER);

        mainLayout.add(subLayout, BorderLayout.CENTER);

        add(mainLayout);
        pack();

        refreshPanel();
    }

    private void refreshListModel() {
        listModel.clear();
        List<T> allObjects = fetch();
        listModel.addAll(allObjects.stream()
                .map(c -> c.name)
                .toList());
    }

    private void newTemplate() {
        create();
        list.setValueIsAdjusting(true);
        refreshListModel();
        list.setSelectedIndex(listModel.size() - 1);
        refreshPanel();
        list.setValueIsAdjusting(false);
    }

    private void save() {
        update();
        int selected = list.getSelectedIndex();
        list.setValueIsAdjusting(true);
        refreshListModel();
        list.setSelectedIndex(selected);
        refreshPanel();
        list.setValueIsAdjusting(false);
    }

    protected abstract CohesivePanel<? extends DisplayedObject> getPanel();

    protected abstract void refreshPanel();

    protected abstract List<T> fetch();

    protected abstract void create();

    protected abstract void update();
}
