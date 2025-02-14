package gui;

import model.FEClass;
import model.Stats.Stat;
import utils.ClassUtils;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

public class ClassPanel extends JPanel {
    private BorderedLabeledTextField nameField;
    private BorderedLabeledTextField tierField;
    private BorderedLabeledSpinner movementField;

    private Map<Stat, BorderedLabeledSpinner> capFields;

    private BorderedLabeledComboBox promotion1Field;
    private BorderedLabeledComboBox promotion2Field;
    private Map<Stat, BorderedLabeledSpinner> bonusFields;

    public ClassPanel() {
        super(new GridLayout(1, 2, 10, 10));
    }

    public void fill(FEClass display) {
        removeAll();

        JPanel nameTierAndCaps = new JPanel();
        nameTierAndCaps.setLayout(new BoxLayout(nameTierAndCaps, BoxLayout.Y_AXIS));
        JPanel promotionsAndBonuses = new JPanel();
        promotionsAndBonuses.setLayout(new BoxLayout(promotionsAndBonuses, BoxLayout.Y_AXIS));

        nameField = new BorderedLabeledTextField("Name");
        tierField = new BorderedLabeledTextField("Tier");
        movementField = new BorderedLabeledSpinner("Movement", new SpinnerNumberModel(display.movement, 0, 10, 1));
        capFields = Map.of(
                Stat.HP, new BorderedLabeledSpinner("HP", new SpinnerNumberModel(display.caps.hitpoints, 0, 120, 1)),
                Stat.STR, new BorderedLabeledSpinner("Strength", new SpinnerNumberModel(display.caps.strength, 0, 50, 1)),
                Stat.MAG, new BorderedLabeledSpinner("Magic", new SpinnerNumberModel(display.caps.magic, 0, 50, 1)),
                Stat.SKL, new BorderedLabeledSpinner("Skill", new SpinnerNumberModel(display.caps.skill, 0, 50, 1)),
                Stat.SPD, new BorderedLabeledSpinner("Speed", new SpinnerNumberModel(display.caps.speed, 0, 50, 1)),
                Stat.DEF, new BorderedLabeledSpinner("Defence", new SpinnerNumberModel(display.caps.defence, 0, 50, 1)),
                Stat.RES, new BorderedLabeledSpinner("Resistance", new SpinnerNumberModel(display.caps.resistance, 0, 50, 1)));
        
        promotion1Field = new BorderedLabeledComboBox("Promotion 1", ClassUtils.getTier(display.tier + 1), display.promotion1 == null ? null : display.promotion1.name);
        promotion2Field = new BorderedLabeledComboBox("Promotion 2", ClassUtils.getTier(display.tier + 1), display.promotion2 == null ? null : display.promotion2.name);
        bonusFields = Map.of(
                Stat.HP, new BorderedLabeledSpinner("HP", new SpinnerNumberModel(display.bonuses.hitpoints, 0, 120, 1)),
                Stat.STR, new BorderedLabeledSpinner("Strength", new SpinnerNumberModel(display.bonuses.strength, 0, 50, 1)),
                Stat.MAG, new BorderedLabeledSpinner("Magic", new SpinnerNumberModel(display.bonuses.magic, 0, 50, 1)),
                Stat.SKL, new BorderedLabeledSpinner("Skill", new SpinnerNumberModel(display.bonuses.skill, 0, 50, 1)),
                Stat.SPD, new BorderedLabeledSpinner("Speed", new SpinnerNumberModel(display.bonuses.speed, 0, 50, 1)),
                Stat.DEF, new BorderedLabeledSpinner("Defence", new SpinnerNumberModel(display.bonuses.defence, 0, 50, 1)),
                Stat.RES, new BorderedLabeledSpinner("Resistance", new SpinnerNumberModel(display.bonuses.resistance, 0, 50, 1)),
                Stat.CON, new BorderedLabeledSpinner("Constitution", new SpinnerNumberModel(display.bonuses.constitution, 0, 25, 1)));

        nameTierAndCaps.add(nameField);
        nameTierAndCaps.add(tierField);
        nameTierAndCaps.add(movementField);
        BorderedLabel classCapsLabel = new BorderedLabel("Class caps:");
        nameTierAndCaps.add(classCapsLabel);
        capFields.forEach((_, field) -> nameTierAndCaps.add(field));
        
        promotionsAndBonuses.add(promotion1Field);
        promotionsAndBonuses.add(promotion2Field);
        BorderedLabel promotionBonusesLabel = new BorderedLabel("Promotion bonuses:");
        promotionsAndBonuses.add(promotionBonusesLabel);
        bonusFields.forEach((_, field) -> promotionsAndBonuses.add(field));

        nameField.inner.setText(display.name);
        tierField.inner.setText(Integer.toString(display.tier));
        tieActionListeners(display);

        add(nameTierAndCaps);
        add(promotionsAndBonuses);

        revalidate();
        repaint();
    }

    private void tieActionListeners(FEClass display) {
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
