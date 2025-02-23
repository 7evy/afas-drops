package gui.frame;

import db.SQLiteRepository;
import gui.panel.ClassPanel;
import model.FEClass;
import utils.ClassUtils;

import java.util.List;

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
    protected List<FEClass> fetch() {
        return SQLiteRepository.fetchAllClasses();
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
