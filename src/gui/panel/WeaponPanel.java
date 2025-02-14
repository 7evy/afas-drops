package gui.panel;

import gui.dumb.BorderedLabel;
import gui.dumb.BorderedLabeledSpinner;
import gui.dumb.BorderedLabeledTextField;
import model.FEWeapon;
import model.Stats;

import javax.swing.*;
import java.util.Map;

public class WeaponPanel extends CohesivePanel<FEWeapon> {
    private BorderedLabeledTextField nameField;
    private BorderedLabeledSpinner mightField;
    private BorderedLabeledSpinner hitField;
    private BorderedLabeledSpinner critField;
    private BorderedLabeledSpinner weightField;
    private BorderedLabeledSpinner minRangeField;
    private BorderedLabeledSpinner maxRangeField;

    private Map<Stats.Stat, BorderedLabeledSpinner> bonusFields;

    public WeaponPanel() {
        super(1, 2);
    }

    protected void fill(FEWeapon display) {
        JPanel nameAndCharacteristics = new JPanel();
        nameAndCharacteristics.setLayout(new BoxLayout(nameAndCharacteristics, BoxLayout.Y_AXIS));
        JPanel bonusStats = new JPanel();
        bonusStats.setLayout(new BoxLayout(bonusStats, BoxLayout.Y_AXIS));

        nameField = new BorderedLabeledTextField("Name");
        mightField = new BorderedLabeledSpinner("Might", new SpinnerNumberModel(display.might, 0, 50, 1));
        hitField = new BorderedLabeledSpinner("Hit bonus", new SpinnerNumberModel(display.hit, 0, 200, 1));
        critField = new BorderedLabeledSpinner("Crit bonus", new SpinnerNumberModel(display.crit, 0, 200, 1));
        weightField = new BorderedLabeledSpinner("Weight", new SpinnerNumberModel(display.weight, 0, 50, 1));
        minRangeField = new BorderedLabeledSpinner("Minimum range", new SpinnerNumberModel(display.minRange, 1, 10, 1));
        maxRangeField = new BorderedLabeledSpinner("Maximum range", new SpinnerNumberModel(display.maxRange, 1, 10, 1));
        bonusFields = Map.of(
                Stats.Stat.STR, new BorderedLabeledSpinner("Strength", new SpinnerNumberModel(display.bonuses.strength, 0, 50, 1)),
                Stats.Stat.MAG, new BorderedLabeledSpinner("Magic", new SpinnerNumberModel(display.bonuses.magic, 0, 50, 1)),
                Stats.Stat.SKL, new BorderedLabeledSpinner("Skill", new SpinnerNumberModel(display.bonuses.skill, 0, 50, 1)),
                Stats.Stat.SPD, new BorderedLabeledSpinner("Speed", new SpinnerNumberModel(display.bonuses.speed, 0, 50, 1)),
                Stats.Stat.LUK, new BorderedLabeledSpinner("Luck", new SpinnerNumberModel(display.bonuses.luck, 0, 50, 1)),
                Stats.Stat.DEF, new BorderedLabeledSpinner("Defence", new SpinnerNumberModel(display.bonuses.defence, 0, 50, 1)),
                Stats.Stat.RES, new BorderedLabeledSpinner("Resistance", new SpinnerNumberModel(display.bonuses.resistance, 0, 50, 1)));

        nameAndCharacteristics.add(nameField);
        nameAndCharacteristics.add(mightField);
        nameAndCharacteristics.add(hitField);
        nameAndCharacteristics.add(critField);
        nameAndCharacteristics.add(weightField);
        nameAndCharacteristics.add(minRangeField);
        nameAndCharacteristics.add(maxRangeField);

        BorderedLabel bonusStatsLabel = new BorderedLabel("Stat bonuses:");
        bonusStats.add(bonusStatsLabel);
        bonusFields.forEach((_, field) -> bonusStats.add(field));

        nameField.inner.setText(display.name);

        add(nameAndCharacteristics);
        add(bonusStats);
    }

    protected void tieActionListeners(FEWeapon display) {
        nameField.addActionListener(() -> display.name = nameField.inner.getText());

        mightField.addChangeListener(() ->
                display.might = (Integer) mightField.inner.getValue(), true);

        hitField.addChangeListener(() ->
                display.hit = (Integer) hitField.inner.getValue(), true);

        critField.addChangeListener(() ->
                display.crit = (Integer) critField.inner.getValue(), true);

        weightField.addChangeListener(() ->
                display.weight = (Integer) weightField.inner.getValue(), true);

        minRangeField.addChangeListener(() ->
                display.minRange = (Integer) minRangeField.inner.getValue(), true);

        maxRangeField.addChangeListener(() ->
                display.maxRange = (Integer) maxRangeField.inner.getValue(), true);

        bonusFields.forEach((stat, field) -> field.addChangeListener(() ->
                display.bonuses.set(stat, (Integer) field.inner.getValue()), true));
    }
}
