package gui.panel;

import gui.dumb.BorderedLabel;
import gui.dumb.BorderedLabeledSpinner;
import gui.dumb.BorderedLabeledTextField;
import model.FEWeapon;
import model.Stats;
import model.Stats.Stat;

import javax.swing.*;

import java.util.LinkedHashMap;
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
        super(1, 2, 10, 10);
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
        bonusFields = new LinkedHashMap<>();
        for (Stat stat : Stat.values()) {
            if (stat != Stat.HP && stat != Stat.CON) {
                bonusFields.put(stat, new BorderedLabeledSpinner(stat.label,
                        new SpinnerNumberModel(display.bonuses.get(stat), 0, 50, 1)));
            }
        }
        
        nameAndCharacteristics.add(nameField);
        nameAndCharacteristics.add(mightField);
        nameAndCharacteristics.add(hitField);
        nameAndCharacteristics.add(critField);
        nameAndCharacteristics.add(weightField);
        nameAndCharacteristics.add(minRangeField);
        nameAndCharacteristics.add(maxRangeField);

        BorderedLabel bonusStatsLabel = new BorderedLabel("Stat bonuses:");
        bonusStats.add(bonusStatsLabel);
        bonusFields.forEach((stat, field) -> bonusStats.add(field));

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
