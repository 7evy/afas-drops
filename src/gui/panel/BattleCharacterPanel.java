package gui.panel;

import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedLabeledSpinner;
import init.Main;
import model.Affinity;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;
import model.Stats;
import model.Stats.Stat;
import model.Support;
import model.SupportRank;
import utils.ClassUtils;
import utils.WeaponUtils;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BattleCharacterPanel extends CohesivePanel<FECharacter> {
    private BorderedLabeledSpinner levelField;
    private BorderedLabeledComboBox classField;
    // TODO move up
    private BorderedLabeledComboBox weaponField;
    private JComboBox<SupportRank> supportRankField;
    private JComboBox<Affinity> supportAffinityField;

    private JPanel editableStatsPanel;
    private Map<Stat, BorderedLabeledSpinner> statFields;

    public BattleCharacterPanel() {
        super(1, 2);
    }

    @Override
    protected void fill(FECharacter display) {
        JPanel metadataPanel = new JPanel();
        metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.Y_AXIS));
        editableStatsPanel = new JPanel();
        editableStatsPanel.setLayout(new BoxLayout(editableStatsPanel, BoxLayout.Y_AXIS));
        JPanel supportPanel = new JPanel(new GridLayout(2, 1));
        JPanel supportSubPanel = new JPanel();
        supportSubPanel.setLayout(new BoxLayout(supportSubPanel, BoxLayout.X_AXIS));

        List<String> weaponNames = Main.WEAPONS.stream().map(w -> w.name).toList();
        levelField = new BorderedLabeledSpinner("Level", new SpinnerNumberModel(1, 1, 20, 1));
        classField = new BorderedLabeledComboBox("Class",
                ClassUtils.getPromotionTree(display.baseClass.name),
                display.baseClass.name);
        weaponField = new BorderedLabeledComboBox("Weapon", weaponNames, weaponNames.getFirst());
        supportRankField = new JComboBox<>(SupportRank.values());
        supportAffinityField = new JComboBox<>(Affinity.values());

        refreshStatFields(display, display.baseClass);

        supportSubPanel.add(supportRankField);
        supportSubPanel.add(supportAffinityField);
        supportPanel.add(new JLabel("Support bonus:"));
        supportPanel.add(supportSubPanel);

        metadataPanel.add(classField);
        metadataPanel.add(levelField);
        metadataPanel.add(weaponField);
        metadataPanel.add(supportPanel);

        add(metadataPanel);
        add(editableStatsPanel);
    }

    @Override
    protected void tieActionListeners(FECharacter display) {
        levelField.addChangeListener(() -> actualizeStats(display), false);
        classField.addActionListener(() -> actualizeStats(display), false);
    }

    public FEWeapon weapon() {
        return WeaponUtils.findByName((String) weaponField.inner.getSelectedItem());
    }

    public Support support(Affinity affinity) {
        return new Support(
                (SupportRank) supportRankField.getSelectedItem(),
                (Affinity) supportAffinityField.getSelectedItem(),
                affinity);
    }

    public Stats stats() {
        Stats stats = new Stats();
        for (Stat stat : Stat.values()) {
            stats.set(stat, (int) statFields.get(stat).inner.getValue());
        }
        return stats;
    }

    private void actualizeStats(FECharacter display) {
        editableStatsPanel.removeAll();
        refreshStatFields(display, ClassUtils.findByName((String) classField.inner.getSelectedItem()));
    }

    private void refreshStatFields(FECharacter display, FEClass selectedClass) {
        statFields = new LinkedHashMap<>();
        Stats stats = computeStats(display, selectedClass);
        for (Stat stat : Stat.values()) {
            statFields.put(stat, new BorderedLabeledSpinner(stat.label,
                    new SpinnerNumberModel(stats.get(stat), 0, selectedClass.caps.get(stat), 1)));
        }
        statFields.forEach((_, field) -> editableStatsPanel.add(field));
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
