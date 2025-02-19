package gui.panel;

import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedLabeledSpinner;
import gui.dumb.BorderedPanel;
import init.Main;
import model.Affinity;
import model.FECharacter;
import model.FEClass;
import model.Stats;
import model.Stats.Stat;
import model.SupportRank;
import utils.ClassUtils;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleCharacterPanel extends CohesivePanel<FECharacter> {
    private BorderedLabeledSpinner levelField;
    private BorderedLabeledComboBox classField;
    private BorderedLabeledComboBox weaponField;
    private BorderedLabeledComboBox supportRankField;
    private BorderedLabeledComboBox supportAffinityField;

    private BorderedPanel editableStatsPanel;
    private Map<Stat, BorderedLabeledSpinner> statFields;

    public BattleCharacterPanel() {
        super(1, 2);
    }

    @Override
    protected void fill(FECharacter display) {
        JPanel metadataPanel = new JPanel();
        metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.Y_AXIS));
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        JPanel supportPanel = new JPanel();
        supportPanel.setLayout(new GridLayout(2, 1));
        JPanel supportSubPanel = new JPanel();
        supportSubPanel.setLayout(new BoxLayout(supportSubPanel, BoxLayout.X_AXIS));

        List<String> weaponNames = Main.WEAPONS.stream().map(w -> w.name).toList();
        levelField = new BorderedLabeledSpinner("Level", new SpinnerNumberModel(1, 1, 20, 1));
        classField = new BorderedLabeledComboBox("Class",
                ClassUtils.getPromotionTree(display.baseClass.name),
                display.baseClass.name);
        weaponField = new BorderedLabeledComboBox("Weapon", weaponNames, weaponNames.getFirst());
        supportRankField = new BorderedLabeledComboBox("Rank",
                Arrays.stream(SupportRank.values()).map(Enum::name).toList(),
                SupportRank.None.name());
        supportAffinityField = new BorderedLabeledComboBox("Affinity",
                Arrays.stream(Affinity.values()).map(Enum::name).toList(),
                Affinity.Light.name());

        statFields = new HashMap<>();
        Stats stats = computeStats(display, display.baseClass);
        for (Stat stat : Stat.values()) {
            statFields.put(stat, new BorderedLabeledSpinner(stat.label,
                    new SpinnerNumberModel(stats.get(stat), 0, display.baseClass.caps.get(stat), 1)));
        }

        supportSubPanel.add(supportRankField);
        supportSubPanel.add(supportAffinityField);
        supportPanel.add(new JLabel("Support bonus:"));
        supportPanel.add(supportSubPanel);

        metadataPanel.add(classField);
        metadataPanel.add(levelField);
        metadataPanel.add(weaponField);
        metadataPanel.add(supportPanel);

        statFields.forEach((_, field) -> statsPanel.add(field));

        add(metadataPanel);
        add(statsPanel);
    }

    @Override
    protected void tieActionListeners(FECharacter display) {

    }

    private void actualizeStats(FECharacter display) {
        FEClass selectedClass = ClassUtils.findByName((String) classField.inner.getSelectedItem());
        Stats stats = computeStats(display, selectedClass);
        for (Stat stat : Stat.values()) {
            statFields.put(stat, new BorderedLabeledSpinner(stat.label,
                    new SpinnerNumberModel(stats.get(stat), 0, selectedClass.caps.get(stat), 1)));
        }
    }

    private Stats computeStats(FECharacter character, FEClass selectedClass) {
        int level = (int) levelField.inner.getValue();
        if (selectedClass.tier == 1) {
            return Stats.computeBaseClassAtLevel(character, level);
        }
        if (selectedClass.tier == 2) {
            return Stats.computeSecondClassAtLevel(character, level, selectedClass.bonuses, selectedClass.caps);
        }
        FEClass secondClass = Main.CLASSES.stream().filter(c -> c.promotion1.equals(selectedClass) || c.promotion2.equals(selectedClass)).findFirst().orElseThrow();
        return Stats.computeThirdClassAtLevel(character, level, secondClass.bonuses,selectedClass.bonuses, secondClass.caps, selectedClass.caps);
    }
}
