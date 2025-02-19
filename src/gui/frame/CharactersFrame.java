package gui.frame;

import db.SQLiteRepository;
import gui.panel.CharacterPanel;
import gui.panel.CohesivePanel;
import init.Main;
import model.DisplayCharacter;
import model.DisplayedObject;
import model.FECharacter;
import model.FEClass;
import utils.CharacterUtils;
import utils.ClassUtils;

import java.util.List;

public class CharactersFrame extends WritableItemFrame<FECharacter> {

    private final CharacterPanel characterPanel;

    public CharactersFrame() {
        super("Characters", "character", 1850, 700);
        this.characterPanel = new CharacterPanel();
    }

    @Override
    protected CohesivePanel<? extends DisplayedObject> getPanel() {
        return characterPanel;
    }

    @Override
    protected void refreshPanel() {
        object = CharacterUtils.findByName(list.getSelectedValue()).clone();
        List<FEClass> initialPromotions = ClassUtils.getDirectPromotions(object.baseClass.name);
        characterPanel.refresh(new DisplayCharacter(object, initialPromotions.get(0), initialPromotions.get(1)));
    }

    @Override
    protected List<FECharacter> fetch() {
        return SQLiteRepository.fetchAllCharacters(Main.CLASSES);
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
