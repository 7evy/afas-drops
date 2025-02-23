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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassPanel extends CohesivePanel<FEClass> {
    private BorderedLabeledTextField nameField;
    private BorderedLabeledTextField tierField;
    private BorderedLabeledSpinner movementField;

    private Map<Stat, BorderedLabeledSpinner> capFields;

    private BorderedLabeledComboBox promotion1Field;
    private BorderedLabeledComboBox promotion2Field;
    private Map<Stat, BorderedLabeledSpinner> bonusFields;

    private BorderedLabeledComboBox innateSkillField;
    private BorderedLabeledComboBox acquiredSkillField;

    public ClassPanel() {
        super(1, 2);
    }

    protected void fill(FEClass display) {
        JPanel nameTierAndCaps = new JPanel();
        nameTierAndCaps.setLayout(new BoxLayout(nameTierAndCaps, BoxLayout.Y_AXIS));
        JPanel promotionsAndBonuses = new JPanel();
        promotionsAndBonuses.setLayout(new BoxLayout(promotionsAndBonuses, BoxLayout.Y_AXIS));

        nameField = new BorderedLabeledTextField("Name");
        tierField = new BorderedLabeledTextField("Tier");
        movementField = new BorderedLabeledSpinner("Movement", new SpinnerNumberModel(display.movement, 0, 10, 1));
        capFields = new LinkedHashMap<>();
        for (Stat stat : Stat.values()) {
            if (stat != Stat.LUK && stat != Stat.CON) {
                capFields.put(stat, new BorderedLabeledSpinner(stat.label,
                        new SpinnerNumberModel(display.caps.get(stat), 0, stat == Stat.HP ? 120 : 50, 1)));
            }
        }
        promotion1Field = new BorderedLabeledComboBox("Promotion 1", ClassUtils.getTier(display.tier + 1), display.promotion1 == null ? null : display.promotion1.name);
        promotion2Field = new BorderedLabeledComboBox("Promotion 2", ClassUtils.getTier(display.tier + 1), display.promotion2 == null ? null : display.promotion2.name);
        bonusFields = new LinkedHashMap<>();
        for (Stat stat : Stat.values()) {
            if (stat != Stat.LUK) {
                bonusFields.put(stat, new BorderedLabeledSpinner(stat.label,
                        new SpinnerNumberModel(display.bonuses.get(stat), 0, stat == Stat.HP ? 120 : (stat == Stat.CON ? 25 : 50), 1)));
            }
        }
        innateSkillField = new BorderedLabeledComboBox("Innate skill", Arrays.stream(Skill.values()).map(Skill::name).toList(), display.innateSkill.name());
        acquiredSkillField = new BorderedLabeledComboBox("Acquired skill", Arrays.stream(Skill.values()).map(Skill::name).toList(), display.acquiredSkill.name());

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
        tierField.inner.setText(Integer.toString(display.tier));

        add(nameTierAndCaps);
        add(promotionsAndBonuses);
    }

    protected void tieActionListeners(FEClass display) {
        nameField.addActionListener(() -> display.name = nameField.inner.getText());

        tierField.addActionListener(() -> {
            int tier = Integer.parseInt(tierField.inner.getText());
            display.tier = tier;
            actualizePromotions(tier);
        });

        movementField.addChangeListener(() ->
                display.movement = (Integer) movementField.inner.getValue(), true);

        capFields.forEach((stat, field) -> field.addChangeListener(() ->
                display.caps.set(stat, (Integer) field.inner.getValue()), true));

        promotion1Field.addActionListener(() -> display.promotion1 =
                ClassUtils.findByName((String) promotion1Field.inner.getSelectedItem()), true);

        promotion2Field.addActionListener(() -> display.promotion2 =
                ClassUtils.findByName((String) promotion2Field.inner.getSelectedItem()), true);

        bonusFields.forEach((stat, field) -> field.addChangeListener(() ->
                display.bonuses.set(stat, (Integer) field.inner.getValue()), true));

        innateSkillField.addActionListener(() -> display.innateSkill =
                Skill.valueOf((String) innateSkillField.inner.getSelectedItem()), true);

        acquiredSkillField.addActionListener(() -> display.acquiredSkill =
                Skill.valueOf((String) acquiredSkillField.inner.getSelectedItem()), true);
    }

    private void actualizePromotions(int tier) {
        List<String> possiblePromotions = ClassUtils.getTier(tier + 1);
        promotion1Field.inner.removeAllItems();
        promotion2Field.inner.removeAllItems();
        possiblePromotions.forEach(c -> {
            promotion1Field.inner.addItem(c);
            promotion2Field.inner.addItem(c);
        });
    }
}
