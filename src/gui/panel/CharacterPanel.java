package gui.panel;

import gui.dumb.BorderedLabel;
import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedLabeledSpinner;
import gui.dumb.BorderedLabeledTextField;
import init.Main;
import model.Affinity;
import model.DisplayCharacter;
import model.FEClass;
import model.Stats;
import model.Stats.Stat;
import utils.ClassUtils;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharacterPanel extends CohesivePanel<DisplayCharacter> {
    private BorderedLabeledTextField nameField;
    private BorderedLabeledTextField originField;
    private Map<Stat, BorderedLabeledSpinner> baseFields;

    private BorderedLabeledSpinner baseLevelField;
    private BorderedLabeledComboBox affinityField;
    private Map<Stat, BorderedLabeledSpinner> growthFields;

    private JPanel baseClassAverageStats;
    private JPanel secondClassAverageStats;
    private JPanel thirdClassAverageStats;
    private BorderedLabeledComboBox baseClassField;
    private BorderedLabeledComboBox secondClassField;
    private BorderedLabeledComboBox thirdClassField;
    private BorderedLabeledSpinner baseClassLevelField;
    private BorderedLabeledSpinner secondClassLevelField;
    private BorderedLabeledSpinner thirdClassLevelField;
    private ReadOnlyStatsComponents maxStats0;
    private ReadOnlyStatsComponents maxStats1;
    private ReadOnlyStatsComponents maxStats2;

    public CharacterPanel() {
        super(1, 5);
    }

    protected void fill(DisplayCharacter display) {
        JPanel nameOriginAndGrowths = new JPanel();
        nameOriginAndGrowths.setLayout(new BoxLayout(nameOriginAndGrowths, BoxLayout.Y_AXIS));
        JPanel levelAffinityAndBases = new JPanel();
        levelAffinityAndBases.setLayout(new BoxLayout(levelAffinityAndBases, BoxLayout.Y_AXIS));

        baseClassAverageStats = new JPanel();
        baseClassAverageStats.setLayout(new BoxLayout(baseClassAverageStats, BoxLayout.Y_AXIS));
        secondClassAverageStats = new JPanel();
        secondClassAverageStats.setLayout(new BoxLayout(secondClassAverageStats, BoxLayout.Y_AXIS));
        thirdClassAverageStats = new JPanel();
        thirdClassAverageStats.setLayout(new BoxLayout(thirdClassAverageStats, BoxLayout.Y_AXIS));

        nameField = new BorderedLabeledTextField("Name");
        originField = new BorderedLabeledTextField("Origin");
        baseFields = Map.of(
                Stat.HP, new BorderedLabeledSpinner("HP", new SpinnerNumberModel(display.data.bases.hitpoints, 0, 50, 1)),
                Stat.STR, new BorderedLabeledSpinner("Strength", new SpinnerNumberModel(display.data.bases.strength, 0, 50, 1)),
                Stat.MAG, new BorderedLabeledSpinner("Magic", new SpinnerNumberModel(display.data.bases.magic, 0, 50, 1)),
                Stat.SKL, new BorderedLabeledSpinner("Skill", new SpinnerNumberModel(display.data.bases.skill, 0, 50, 1)),
                Stat.SPD, new BorderedLabeledSpinner("Speed", new SpinnerNumberModel(display.data.bases.speed, 0, 50, 1)),
                Stat.LUK, new BorderedLabeledSpinner("Luck", new SpinnerNumberModel(display.data.bases.luck, 0, 50, 1)),
                Stat.DEF, new BorderedLabeledSpinner("Defence", new SpinnerNumberModel(display.data.bases.defence, 0, 50, 1)),
                Stat.RES, new BorderedLabeledSpinner("Resistance", new SpinnerNumberModel(display.data.bases.resistance, 0, 50, 1)),
                Stat.CON, new BorderedLabeledSpinner("Constitution", new SpinnerNumberModel(display.data.bases.constitution, 0, 25, 1)));
        
        affinityField = new BorderedLabeledComboBox("Affinity",
                Arrays.stream(Affinity.values()).map(Enum::name).toList(),
                display.data.affinity.name());
        baseLevelField = new BorderedLabeledSpinner("Starting level", new SpinnerNumberModel(display.data.baseLevel, 1, 60, 1), 60);
        growthFields = Map.of(
                Stat.HP, new BorderedLabeledSpinner("HP", new SpinnerNumberModel(display.data.growths.hitpoints, 0, 250, 5)),
                Stat.STR, new BorderedLabeledSpinner("Strength", new SpinnerNumberModel(display.data.growths.strength, 0, 250, 5)),
                Stat.MAG, new BorderedLabeledSpinner("Magic", new SpinnerNumberModel(display.data.growths.magic, 0, 250, 5)),
                Stat.SKL, new BorderedLabeledSpinner("Skill", new SpinnerNumberModel(display.data.growths.skill, 0, 250, 5)),
                Stat.SPD, new BorderedLabeledSpinner("Speed", new SpinnerNumberModel(display.data.growths.speed, 0, 250, 5)),
                Stat.LUK, new BorderedLabeledSpinner("Luck", new SpinnerNumberModel(display.data.growths.luck, 0, 250, 5)),
                Stat.DEF, new BorderedLabeledSpinner("Defence", new SpinnerNumberModel(display.data.growths.defence, 0, 250, 5)),
                Stat.RES, new BorderedLabeledSpinner("Resistance", new SpinnerNumberModel(display.data.growths.resistance, 0, 250, 5)));

        nameOriginAndGrowths.add(nameField);
        nameOriginAndGrowths.add(originField);
        BorderedLabel growthsLabel = new BorderedLabel("Character growths:");
        nameOriginAndGrowths.add(growthsLabel);
        growthFields.forEach((_, field) -> nameOriginAndGrowths.add(field));
        
        levelAffinityAndBases.add(baseLevelField);
        levelAffinityAndBases.add(affinityField);
        BorderedLabel basesLabel = new BorderedLabel("Character bases:");
        levelAffinityAndBases.add(basesLabel);
        baseFields.forEach((_, field) -> levelAffinityAndBases.add(field));
        
        baseClassLevelField = new BorderedLabeledSpinner("At level", new SpinnerNumberModel(20, 1, 20, 1));
        secondClassLevelField = new BorderedLabeledSpinner("At level", new SpinnerNumberModel(20, 1, 20, 1));
        thirdClassLevelField = new BorderedLabeledSpinner("At level", new SpinnerNumberModel(20, 1, 20, 1));
        baseClassField = new BorderedLabeledComboBox("Starting class",
                Main.CLASSES.stream().map(c -> c.name).toList(),
                display.data.baseClass.name);

        actualizeSecondClass(display);
        actualizeThirdClass(display);
        actualizeBaseClassStats(display);
        actualizeSecondClassStats(display);
        actualizeThirdClassStats(display);

        nameField.inner.setText(display.data.name);
        originField.inner.setText(display.data.origin);
        affinityField.inner.setSelectedItem(display.data.affinity.name());
        baseClassField.inner.setSelectedItem(display.data.baseClass);

        add(nameOriginAndGrowths);
        add(levelAffinityAndBases);
        add(baseClassAverageStats);
        add(secondClassAverageStats);
        add(thirdClassAverageStats);
    }

    protected void tieActionListeners(DisplayCharacter display) {
        nameField.addActionListener(() ->
                display.data.name = nameField.inner.getText());

        originField.addActionListener(() ->
                display.data.origin = originField.inner.getText());

        affinityField.addActionListener(() -> display.data.affinity =
                Affinity.valueOf((String) affinityField.inner.getSelectedItem()), true);

        baseFields.forEach((stat, field) -> field.addChangeListener(() -> {
            display.data.bases.set(stat, (Integer) field.inner.getValue());
            actualizeOneBaseStat(stat, display);
        }, true));

        baseClassField.addActionListener(() -> {
            display.data.baseClass = ClassUtils.findByName(
                (String) baseClassField.inner.getSelectedItem()
            );
            actualizeSecondClass(display);
            actualizeThirdClass(display);
            actualizeBaseClassStats(display);
            actualizeSecondClassStats(display);
            actualizeThirdClassStats(display);
        }, true);

        secondClassField.addActionListener(() -> {
            display.secondClass = ClassUtils.findByName(
                (String) secondClassField.inner.getSelectedItem()
            );
            actualizeThirdClass(display);
            actualizeSecondClassStats(display);
            actualizeThirdClassStats(display);
        }, false);

        thirdClassField.addActionListener(() -> {
            display.thirdClass = ClassUtils.findByName(
                (String) thirdClassField.inner.getSelectedItem()
            );
            actualizeThirdClassStats(display);
        }, false);

        baseClassLevelField.addChangeListener(() -> actualizeBaseClassStats(display), false);

        secondClassLevelField.addChangeListener(() -> actualizeSecondClassStats(display), false);

        thirdClassLevelField.addChangeListener(() -> actualizeThirdClassStats(display), false);

        baseLevelField.addChangeListener(() -> {
            display.data.baseLevel = (Integer) baseLevelField.inner.getValue();
            actualizeBaseClassStats(display);
            actualizeSecondClassStats(display);
            actualizeThirdClassStats(display);
        }, true);

        growthFields.forEach((stat, field) -> field.addChangeListener(() -> {
            display.data.growths.set(stat, (Integer) field.inner.getValue());
            actualizeOneBaseStat(stat, display);
        }, true));
    }

    private void actualizeSecondClass(DisplayCharacter display) {
        List<FEClass> options = ClassUtils.getPromotions((String) baseClassField.inner.getSelectedItem());
        secondClassField = new BorderedLabeledComboBox("Second class",
                options.stream().filter(Objects::nonNull).map(c -> c.name).toList(),
                display.secondClass == null ? null : display.secondClass.name);
        secondClassField.inner.setSelectedItem(0);
        display.secondClass = options.getFirst();
    }

    private void actualizeThirdClass(DisplayCharacter display) {
        List<FEClass> options = ClassUtils.getPromotions((String) secondClassField.inner.getSelectedItem());
        thirdClassField = new BorderedLabeledComboBox("Third class",
                options.stream().filter(Objects::nonNull).map(c -> c.name).toList(),
                display.thirdClass == null ? null : display.thirdClass.name);
        thirdClassField.inner.setSelectedItem(0);
        display.thirdClass = options.getFirst();
    }

    private void actualizeBaseClassStats(DisplayCharacter display) {
        int level = (Integer) baseClassLevelField.inner.getValue();
        baseClassAverageStats.removeAll();
        baseClassAverageStats.add(baseClassField);
        baseClassAverageStats.add(baseClassLevelField);
        BorderedLabel label = new BorderedLabel("Average stats at level " + level + ":");
        baseClassAverageStats.add(label);
        maxStats0 = new ReadOnlyStatsComponents(display, level, 0);
        maxStats0.addToPanel(baseClassAverageStats);
        baseClassAverageStats.revalidate();
        baseClassAverageStats.repaint();
    }

    private void actualizeSecondClassStats(DisplayCharacter display) {
        int level = (Integer) secondClassLevelField.inner.getValue();
        secondClassAverageStats.removeAll();
        secondClassAverageStats.add(secondClassField);
        secondClassAverageStats.add(secondClassLevelField);
        BorderedLabel label = new BorderedLabel("Average stats at level " + level + ":");
        secondClassAverageStats.add(label);
        if (display.secondClass != null) {
            maxStats1 = new ReadOnlyStatsComponents(display, level, 1);
            maxStats1.addToPanel(secondClassAverageStats);
        }
        secondClassAverageStats.revalidate();
        secondClassAverageStats.repaint();
    }

    private void actualizeThirdClassStats(DisplayCharacter display) {
        int level = (Integer) thirdClassLevelField.inner.getValue();
        thirdClassAverageStats.removeAll();
        thirdClassAverageStats.add(thirdClassField);
        thirdClassAverageStats.add(thirdClassLevelField);
        BorderedLabel label = new BorderedLabel("Average stats at level " + level + ":");
        thirdClassAverageStats.add(label);
        if (display.thirdClass != null) {
            maxStats2 = new ReadOnlyStatsComponents(display, level, 2);
            maxStats2.addToPanel(thirdClassAverageStats);
        }
        thirdClassAverageStats.revalidate();
        thirdClassAverageStats.repaint();
    }

    private void actualizeOneBaseStat(Stat stat, DisplayCharacter display) {
        int value = display.data.bases.get(stat);
        int growth = display.data.growths.get(stat);
        int cap0 = display.data.baseClass.caps.get(stat);
        int startLevel = display.data.baseLevel;
        int tier = display.data.baseClass.tier;
        int levelDiff = 20 - (startLevel - 1) % 20 - 1;
        int stat0 = Stats.computeStat(value, 0, growth, cap0, levelDiff);
        maxStats0.update(stat, stat0);
        if (tier < 3) {
            int bonus1 = display.secondClass.bonuses.get(stat);
            int cap1 = display.secondClass.caps.get(stat);
            int stat1 = Stats.computeStat(stat0, bonus1, growth, cap1, 19);
            maxStats1.update(stat, stat1);
            if (tier == 1) {
                int bonus2 = display.thirdClass.bonuses.get(stat);
                int cap2 = display.thirdClass.caps.get(stat);
                maxStats2.update(stat, Stats.computeStat(stat1, bonus2, growth, cap2, 19));
            }
        }
    }
}
