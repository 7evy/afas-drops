package gui.panel;

import model.FEWeapon;
import model.Stats;
import model.Support.SupportBonus;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class BattleForecastPanel extends JPanel {
    private JPanel hitPanel;
    private JPanel critPanel;
    private JPanel damagePanel;
    private JPanel hpPanel;
    private JPanel winRatePanel;

    private JLabel hitRight;
    private JLabel hitLeft;
    private JLabel critRight;
    private JLabel critLeft;
    private JLabel damageRight;
    private JLabel damageLeft;
    private JLabel hpRight;
    private JLabel hpLeft;
    private JLabel winRateRight;
    private JLabel winRateLeft;

    public BattleForecastPanel() {
        super(new GridLayout(1, 3, 10, 10));
        hitPanel = new JPanel();
        hitPanel.setLayout(new GridLayout(1, 2));
        hitRight = new JLabel();
        hitLeft = new JLabel();
        hitPanel.add(hitRight);
        hitPanel.add(hitLeft);

        critPanel = new JPanel();
        critPanel.setLayout(new GridLayout(1, 2));
        critRight = new JLabel();
        critLeft = new JLabel();
        critPanel.add(critRight);
        critPanel.add(critLeft);

        damagePanel = new JPanel();
        damagePanel.setLayout(new GridLayout(1, 2));
        damageRight = new JLabel();
        damageLeft = new JLabel();
        damagePanel.add(damageRight);
        damagePanel.add(damageLeft);

        hpPanel = new JPanel();
        hpPanel.setLayout(new GridLayout(1, 2));
        hpRight = new JLabel();
        hpLeft = new JLabel();
        hpPanel.add(hpRight);
        hpPanel.add(hpLeft);

        winRatePanel = new JPanel();
        winRatePanel.setLayout(new GridLayout(1, 2));
        winRateRight = new JLabel();
        winRateLeft = new JLabel();
        winRatePanel.add(winRateRight);
        winRatePanel.add(winRateLeft);
    }

    public void refresh(Stats stats1, FEWeapon weapon1, SupportBonus support1,
                        Stats stats2, FEWeapon weapon2, SupportBonus support2) {
        int attackSpeed1 = stats1.speed - stats1.constitution > weapon1.weight ? 0 : stats1.constitution - weapon1.weight;
        int attackSpeed2 = stats2.speed - stats2.constitution > weapon2.weight ? 0 : stats2.constitution - weapon2.weight;
        boolean leftDoubles = attackSpeed1 > attackSpeed2 + 3;
        boolean rightDoubles = attackSpeed2 > attackSpeed1 + 3;

        hitLeft.setText(String.valueOf(Math.max(0, Math.min(100,
                (weapon1.hit * 2 + stats1.skill * 4 + stats1.luck + support1.hit
                        - attackSpeed2 * 4 - stats2.luck * 2 - support2.avoid) / 2
        ))));
        hitRight.setText(String.valueOf(Math.max(0, Math.min(100,
                (weapon2.hit * 2 + stats2.skill * 4 + stats2.luck + support2.hit
                        - attackSpeed1 * 4 - stats1.luck * 2 - support1.avoid) / 2
        ))));

        critLeft.setText(String.valueOf(Math.max(0, Math.min(100,
                (stats1.skill + weapon1.crit * 2 + support1.crit
                        - stats2.luck * 2 - support2.dodge) / 2
        ))));
        critRight.setText(String.valueOf(Math.max(0, Math.min(100,
                (stats2.skill + weapon2.crit * 2 + support2.crit
                        - stats1.luck * 2 - support1.dodge) / 2
        ))));

        int damage1 = Math.max(0, stats1.strength + weapon1.might - stats2.defence);
        int damage2 = Math.max(0, stats2.strength + weapon2.might - stats1.defence);
        damageLeft.setText(damage1 + (leftDoubles ? " x2" : ""));
        damageRight.setText(damage2 + (rightDoubles ? " x2" : ""));

//        hpLeft.setText(String.valueOf(stats1.hitpoints - damage2 - (rightDoubles ? damage2 : 0)));
//        hpRight.setText(String.valueOf(stats2.hitpoints - damage1 - (leftDoubles ? damage1 : 0)));
    }

    public void simulate(int maxHp1, int hitRate1, int critRate1, int damage1, boolean leftDoubles,
                         int maxHp2, int hitRate2, int critRate2, int damage2, boolean rightDoubles) {
        List<BattleOutcome> outcomes = new ArrayList<>();
        int totalProbability = 0;

        int hp2 = maxHp2 - damage1;
        if (hp2 <= 0) {
            outcomes.add(new BattleOutcome(maxHp1, 0, hitRate1));
            totalProbability = hitRate1;
        }
    }

    public record BattleOutcome(int hpAttacker, int hpDefender, int percentProbability) {}
}
