package gui;

import model.DisplayCharacter;
import model.Stats;
import model.Stats.Stat;

import javax.swing.JPanel;
import java.util.List;

public class ReadOnlyStatsComponents {
    private final List<BorderedLabeledTextField> fields;

    public ReadOnlyStatsComponents(DisplayCharacter display, int level, int tierAdvance) {
        Stats stats = switch(tierAdvance) {
            case 0 -> Stats.computeBaseClassAtLevel(display.data, level);
            case 1 -> Stats.computeSecondClassAtLevel(display.data, level, display.secondClass.bonuses, display.secondClass.caps);
            case 2 -> Stats.computeThirdClassAtLevel(display.data, level, display.secondClass.bonuses, display.thirdClass.bonuses, display.secondClass.caps, display.thirdClass.caps);
            default -> throw new RuntimeException("Tier can be 1, 2 or 3");
        };
        fields = List.of(
                new BorderedLabeledTextField("HP", Integer.toString(stats.hitpoints), false),
                new BorderedLabeledTextField("Strength", Integer.toString(stats.strength), false),
                new BorderedLabeledTextField("Magic", Integer.toString(stats.magic), false),
                new BorderedLabeledTextField("Skill", Integer.toString(stats.skill), false),
                new BorderedLabeledTextField("Speed", Integer.toString(stats.speed), false),
                new BorderedLabeledTextField("Luck", Integer.toString(stats.luck), false),
                new BorderedLabeledTextField("Defence", Integer.toString(stats.defence), false),
                new BorderedLabeledTextField("Resistance", Integer.toString(stats.resistance), false),
                new BorderedLabeledTextField("Constitution", Integer.toString(stats.constitution), false));
    }

    public void addToPanel(JPanel panel) {
        fields.forEach(panel::add);
    }

    public void update(Stat stat, int value) {
        fields.get(stat.ordinal()).inner.setText(Integer.toString(value));
    }
}
