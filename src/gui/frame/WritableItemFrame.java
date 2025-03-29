package gui.frame;

import gui.panel.CohesivePanel;
import gui.dumb.BorderedButton;
import gui.dumb.BorderedPanel;
import model.DisplayedObject;
import model.FEObject;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

public abstract class WritableItemFrame<T extends FEObject> extends JFrame {

    protected final DefaultListModel<String> listModel = new DefaultListModel<>();
    protected final JList<String> list;

    protected T object;

    public WritableItemFrame(String title, String label, int width, int height) {
        super(title);
        setPreferredSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        makePanel();

        BorderedButton newTemplateButton = new BorderedButton("New " + label, 50);
        newTemplateButton.addActionListener(this::newTemplate);

        BorderedButton saveButton = new BorderedButton("Write to memory", 50);
        saveButton.addActionListener(this::save);

        BorderedButton refreshButton = new BorderedButton("Refresh", 50);
        refreshButton.addActionListener(this::refreshPanel);

        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);

        BorderedPanel buttonContainer = new BorderedPanel(100, 0);
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
        subLayout.add(new JScrollPane(list), BorderLayout.WEST);
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

    protected abstract void makePanel();

    protected abstract CohesivePanel<? extends DisplayedObject> getPanel();

    protected abstract void refreshPanel();

    protected abstract List<T> fetch();

    protected abstract void create();

    protected abstract void update();
}
