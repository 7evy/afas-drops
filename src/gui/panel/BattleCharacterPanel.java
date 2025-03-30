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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.util.LinkedHashMap;
import java.util.Map;

public class BattleCharacterPanel extends CohesivePanel<FECharacter> {
    private BorderedLabeledSpinner levelField;
    private BorderedLabeledComboBox<FEClass> classField;
    private BorderedLabeledComboBox<FEWeapon> weaponField;
    private BorderedLabeledComboBox<SupportRank> supportRankField;
    private BorderedLabeledComboBox<Affinity> supportAffinityField;

    private JPanel editableStatsPanel;
    private Map<Stat, BorderedLabeledSpinner> statFields;

    public BattleCharacterPanel() {
        super(1, 2, 0, 0);
    }

    @Override
    protected void fill(FECharacter display) {
        levelField = new BorderedLabeledSpinner("Level:", new SpinnerNumberModel(1, 1, 20, 1));
        classField = new BorderedLabeledComboBox<>("Class:",
                ClassUtils.getPromotionTree(display.baseClass),
                display.baseClass);
        weaponField = new BorderedLabeledComboBox<>("Weapon:", Main.WEAPONS.toArray(FEWeapon[]::new), Main.WEAPONS.getFirst());
        supportRankField = new BorderedLabeledComboBox<>("Rank:", SupportRank.values(), SupportRank.None);
        supportAffinityField = new BorderedLabeledComboBox<>("Affinity:", Affinity.values(), Affinity.Light);

        JPanel supportPanel = new JPanel();
        supportPanel.setLayout(new BoxLayout(supportPanel, BoxLayout.Y_AXIS));
        JLabel supportBonusLabel = new JLabel("Support bonus:");
        supportBonusLabel.setAlignmentX(CENTER_ALIGNMENT);
        supportPanel.add(supportBonusLabel);

        JPanel supportSubPanel = new JPanel();
        supportSubPanel.setLayout(new BoxLayout(supportSubPanel, BoxLayout.Y_AXIS));
        supportSubPanel.add(supportRankField);
        supportSubPanel.add(supportAffinityField);
        supportPanel.add(supportSubPanel);

        JPanel metadataPanel = new JPanel();
        metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.Y_AXIS));
        metadataPanel.add(classField);
        metadataPanel.add(levelField);
        metadataPanel.add(weaponField);
        metadataPanel.add(supportPanel);
        add(metadataPanel);

        editableStatsPanel = new JPanel();
        editableStatsPanel.setLayout(new BoxLayout(editableStatsPanel, BoxLayout.Y_AXIS));
        refreshStatFields(display, display.baseClass);
        add(editableStatsPanel);
    }

    @Override
    protected void tieActionListeners(FECharacter display) {
        levelField.addChangeListener(() -> actualizeStats(display), false);
        classField.addActionListener(() -> actualizeStats(display), false);
    }

    public FEClass feClass() {
        return classField.getSelectedItem();
    }

    public FEWeapon weapon() {
        return weaponField.getSelectedItem();
    }

    public Support support(Affinity affinity) {
        return new Support(supportRankField.getSelectedItem(), supportAffinityField.getSelectedItem(), affinity);
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
        refreshStatFields(display, classField.getSelectedItem());
    }

    private void refreshStatFields(FECharacter display, FEClass selectedClass) {
        statFields = new LinkedHashMap<>();
        Stats stats = computeStats(display, selectedClass);
        for (Stat stat : Stat.values()) {
            statFields.put(stat, new BorderedLabeledSpinner(stat.label,
                    new SpinnerNumberModel(stats.get(stat), 0, selectedClass.caps.get(stat), 1)));
        }
        statFields.forEach((stat, field) -> editableStatsPanel.add(field));
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
