package gui.frame;

import db.SQLiteRepository;
import gui.panel.ClassPanel;
import model.FEClass;
import utils.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassesFrame extends WritableItemFrame<FEClass> {

    private ClassPanel classPanel;

    public ClassesFrame() {
        super("Classes", "class", 1000, 750);
    }

    @Override
    protected void makePanel() {
        classPanel = new ClassPanel();
    }

    @Override
    protected ClassPanel getPanel() {
        return classPanel;
    }

    @Override
    protected void refreshPanel() {
        object = ClassUtils.findByName(list.getSelectedValue()).clone();
        classPanel.refresh(object);
    }

    @Override
    protected Set<String> fetch() {
        return SQLiteRepository.fetchAllClasses().keySet();
    }

    @Override
    protected void create() {
        SQLiteRepository.newClass();
    }

    @Override
    protected void update() {
        SQLiteRepository.updateClass(object);
    }
}
