package gui.panel;

import gui.dumb.BorderedLabel;
import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedLabeledSpinner;
import gui.dumb.BorderedLabeledTextField;
import model.FEClass;
import model.Skill;
import model.Stats.Stat;
import utils.ClassUtils;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassPanel extends CohesivePanel<FEClass> {
    private BorderedLabeledTextField nameField;
    private BorderedLabeledSpinner tierField;
    private BorderedLabeledSpinner movementField;

    private Map<Stat, BorderedLabeledSpinner> capFields;

    private BorderedLabeledComboBox<FEClass> promotion1Field;
    private BorderedLabeledComboBox<FEClass> promotion2Field;
    private Map<Stat, BorderedLabeledSpinner> bonusFields;

    private BorderedLabeledComboBox<Skill> innateSkillField;
    private BorderedLabeledComboBox<Skill> acquiredSkillField;

    public ClassPanel() {
        super(1, 2, 0, 10);
    }

    protected void fill(FEClass display) {
        JPanel nameTierAndCaps = new JPanel();
        nameTierAndCaps.setLayout(new BoxLayout(nameTierAndCaps, BoxLayout.Y_AXIS));
        JPanel promotionsAndBonuses = new JPanel();
        promotionsAndBonuses.setLayout(new BoxLayout(promotionsAndBonuses, BoxLayout.Y_AXIS));

        nameField = new BorderedLabeledTextField("Name");
        tierField = new BorderedLabeledSpinner("Tier", new SpinnerNumberModel(display.tier, 1, 3, 1));
        movementField = new BorderedLabeledSpinner("Movement", new SpinnerNumberModel(display.movement, 0, 10, 1));
        capFields = new LinkedHashMap<>();
        for (Stat stat : Stat.values()) {
            if (stat != Stat.LUK && stat != Stat.CON) {
                capFields.put(stat, new BorderedLabeledSpinner(stat.label,
                        new SpinnerNumberModel(display.caps.get(stat), 0, stat == Stat.HP ? 120 : 50, 1)));
            }
        }
        promotion1Field = new BorderedLabeledComboBox<>("Promotion 1", ClassUtils.getTier(display.tier + 1), display.promotion1);
        promotion2Field = new BorderedLabeledComboBox<>("Promotion 2", ClassUtils.getTier(display.tier + 1), display.promotion2);
        bonusFields = new LinkedHashMap<>();
        for (Stat stat : Stat.values()) {
            if (stat != Stat.LUK) {
                bonusFields.put(stat, new BorderedLabeledSpinner(stat.label,
                        new SpinnerNumberModel(display.bonuses.get(stat), 0, stat == Stat.HP ? 120 : (stat == Stat.CON ? 25 : 50), 1)));
            }
        }
        innateSkillField = new BorderedLabeledComboBox<>("Innate skill", Skill.values(), display.innateSkill);
        acquiredSkillField = new BorderedLabeledComboBox<>("Acquired skill", Skill.values(), display.acquiredSkill);

        nameTierAndCaps.add(nameField);
        nameTierAndCaps.add(tierField);
        nameTierAndCaps.add(movementField);
        BorderedLabel classCapsLabel = new BorderedLabel("Class caps:");
        nameTierAndCaps.add(classCapsLabel);
        capFields.forEach((stat, field) -> nameTierAndCaps.add(field));
        nameTierAndCaps.add(innateSkillField);
        
        promotionsAndBonuses.add(promotion1Field);
        promotionsAndBonuses.add(promotion2Field);
        BorderedLabel promotionBonusesLabel = new BorderedLabel("Promotion bonuses:");
        promotionsAndBonuses.add(promotionBonusesLabel);
        bonusFields.forEach((stat, field) -> promotionsAndBonuses.add(field));
        promotionsAndBonuses.add(acquiredSkillField);

        nameField.inner.setText(display.name);

        add(nameTierAndCaps);
        add(promotionsAndBonuses);
    }

    protected void tieActionListeners(FEClass display) {
        nameField.addActionListener(() -> display.name = nameField.inner.getText());

        tierField.addChangeListener(() -> {
            display.tier = (Integer) tierField.inner.getValue();
            actualizePromotions(display.tier);
        }, true);

        movementField.addChangeListener(() ->
                display.movement = (Integer) movementField.inner.getValue(), true);

        capFields.forEach((stat, field) -> field.addChangeListener(() ->
                display.caps.set(stat, (Integer) field.inner.getValue()), true));

        promotion1Field.addActionListener(() ->
                display.promotion1 = promotion1Field.getSelectedItem(), true);

        promotion2Field.addActionListener(() ->
                display.promotion2 = promotion2Field.getSelectedItem(), true);

        bonusFields.forEach((stat, field) -> field.addChangeListener(() ->
                display.bonuses.set(stat, (Integer) field.inner.getValue()), true));

        innateSkillField.addActionListener(() ->
                display.innateSkill = innateSkillField.getSelectedItem(), true);

        acquiredSkillField.addActionListener(() ->
                display.acquiredSkill = acquiredSkillField.getSelectedItem(), true);
    }

    private void actualizePromotions(int tier) {
        FEClass[] possiblePromotions = ClassUtils.getTier(tier + 1);
        promotion1Field.removeAllItems();
        promotion2Field.removeAllItems();
        for (FEClass promotion : possiblePromotions) {
            promotion1Field.addItem(promotion);
            promotion2Field.addItem(promotion);
        };
    }
}
