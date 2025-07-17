package gui.frame;

import db.SQLiteRepository;
import gui.panel.CharacterPanel;
import init.Main;
import model.DisplayCharacter;
import model.FECharacter;
import model.FEClass;
import utils.CharacterUtils;
import utils.ClassUtils;

import java.util.List;
import java.util.Set;

public class CharactersFrame extends WritableItemFrame<FECharacter> {

    private CharacterPanel characterPanel;

    public CharactersFrame() {
        super("Characters", "character", 1850, 700);
    }

    @Override
    protected void makePanel() {
        characterPanel = new CharacterPanel();
    }

    @Override
    protected CharacterPanel getPanel() {
        return characterPanel;
    }

    @Override
    protected void refreshPanel() {
        object = CharacterUtils.findByName(list.getSelectedValue()).clone();
        FEClass[] initialPromotions = ClassUtils.getDirectPromotions(object.baseClass);
        characterPanel.refresh(new DisplayCharacter(object, initialPromotions[0], initialPromotions[1]));
    }

    @Override
    protected Set<String> fetch() {
        return SQLiteRepository.fetchAllCharacters(Main.CLASSES).keySet();
    }

    @Override
    protected void create() {
        SQLiteRepository.newCharacter();
    }

    @Override
    protected void update() {
        SQLiteRepository.updateCharacter(object);
    }
}
