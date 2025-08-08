package gui.panel;

import gui.dumb.BorderedPanel;
import utils.BattleLog;
import utils.BattleOutcome;
import utils.BattleSimulation;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BattleForecastPanel extends JPanel {

    private final JButton previousButton;
    private final JButton nextButton;
    private final JLabel resultProbability;

    private final JLabel hitRateInitiatorLabel;
    private final JLabel critRateInitiatorLabel;
    private final JLabel damageInitiatorLabel;
    private final JLabel hpInitiator;

    private final JLabel hitRateRetaliatorLabel;
    private final JLabel critRateRetaliatorLabel;
    private final JLabel damageRetaliatorLabel;
    private final JLabel hpRetaliator;

    private final JList<String> simulationDetails;

    public BattleForecastPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        BorderedPanel hitPanel = new BorderedPanel(0, 2);
        hitPanel.pad();
        hitPanel.setMaximumSize(new Dimension(400, 45));
        JPanel hitSubPanel = new JPanel();
        hitRateInitiatorLabel = new JLabel();
        JLabel hitRateLabel = new JLabel("    < Hit >    ");
        hitRateRetaliatorLabel = new JLabel();
        hitSubPanel.add(hitRateInitiatorLabel);
        hitSubPanel.add(hitRateLabel);
        hitSubPanel.add(hitRateRetaliatorLabel);
        hitPanel.add(hitSubPanel, BorderLayout.CENTER);

        BorderedPanel critPanel = new BorderedPanel(0, 2);
        critPanel.pad();
        critPanel.setMaximumSize(new Dimension(400, 45));
        JPanel critSubPanel = new JPanel();
        critRateInitiatorLabel = new JLabel();
        JLabel critRateLabel = new JLabel("    < Crit >    ");
        critRateRetaliatorLabel = new JLabel();
        critSubPanel.add(critRateInitiatorLabel);
        critSubPanel.add(critRateLabel);
        critSubPanel.add(critRateRetaliatorLabel);
        critPanel.add(critSubPanel, BorderLayout.CENTER);

        BorderedPanel damagePanel = new BorderedPanel(0, 2);
        damagePanel.pad();
        damagePanel.setMaximumSize(new Dimension(400, 45));
        JPanel damageSubPanel = new JPanel();
        damageInitiatorLabel = new JLabel();
        JLabel damageLabel = new JLabel("   < Damage >   ");
        damageRetaliatorLabel = new JLabel();
        damageSubPanel.add(damageInitiatorLabel);
        damageSubPanel.add(damageLabel);
        damageSubPanel.add(damageRetaliatorLabel);
        damagePanel.add(damageSubPanel, BorderLayout.CENTER);

        BorderedPanel arrowsPanel = new BorderedPanel(0, 2);
        arrowsPanel.pad();
        arrowsPanel.setMaximumSize(new Dimension(400, 70));
        JPanel arrowsSubPanel = new JPanel();
        nextButton = new JButton("▶");
        previousButton = new JButton("◀");
        arrowsSubPanel.add(previousButton);
        arrowsSubPanel.add(nextButton);
        arrowsPanel.add(arrowsSubPanel, BorderLayout.CENTER);

        resultProbability = new JLabel();
        resultProbability.setAlignmentX(CENTER_ALIGNMENT);

        JPanel hpLeftPanel = new JPanel();
        hpLeftPanel.setMaximumSize(new Dimension(400, 45));
        hpInitiator = new JLabel();
        JLabel hpLeftLabel = new JLabel("   < HP left >   ");
        hpRetaliator = new JLabel();
        hpLeftPanel.add(hpInitiator);
        hpLeftPanel.add(hpLeftLabel);
        hpLeftPanel.add(hpRetaliator);

        JPanel simulationDetailsPanel = new JPanel();
        simulationDetailsPanel.setLayout(new BoxLayout(simulationDetailsPanel, BoxLayout.Y_AXIS));
        simulationDetails = new JList<>();
        simulationDetails.setEnabled(false);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.add(arrowsPanel);
        resultPanel.add(resultProbability);
        resultPanel.add(hpLeftPanel);
        resultPanel.add(simulationDetailsPanel);

        add(hitPanel);
        add(critPanel);
        add(damagePanel);
        add(resultPanel);
    }

    public void refresh(BattleSimulation simulation) {
        this.hitRateInitiatorLabel.setText(simulation.initiatorHitRate() + "%");
        this.hitRateRetaliatorLabel.setText(simulation.retaliatorHitRate() + "%");

        this.critRateInitiatorLabel.setText(simulation.initiatorCritRate() + "%");
        this.critRateRetaliatorLabel.setText(simulation.retaliatorCritRate() + "%");

        this.damageInitiatorLabel.setText(simulation.initiatorDamage() + (simulation.initiatorDoubles() ? " x2" : ""));
        this.damageRetaliatorLabel.setText(simulation.retaliatorDamage() + (simulation.retaliatorDoubles() ? " x2" : ""));

        List<BattleOutcome> outcomes = simulation.launch();
        AtomicInteger iterator = new AtomicInteger(0);

        for (ActionListener al : previousButton.getActionListeners()) {
            previousButton.removeActionListener(al);
        }
        previousButton.addActionListener(ignored -> {
            if (iterator.incrementAndGet() >= outcomes.size()) {
                iterator.set(0);
            }
            refreshResultPanel(outcomes.get(iterator.get()), simulation.initiatorStats().hitpoints, simulation.retaliatorStats().hitpoints);
        });
        
        for (ActionListener al : nextButton.getActionListeners()) {
            nextButton.removeActionListener(al);
        }
        nextButton.addActionListener(ignored -> {
            if (iterator.decrementAndGet() < 0) {
                iterator.set(outcomes.size() - 1);
            }
            refreshResultPanel(outcomes.get(iterator.get()), simulation.initiatorStats().hitpoints, simulation.retaliatorStats().hitpoints);
        });

        refreshResultPanel(outcomes.getFirst(), simulation.initiatorStats().hitpoints, simulation.retaliatorStats().hitpoints);

        revalidate();
        repaint();
    }

    private void refreshResultPanel(BattleOutcome result, int maxHpInitiator, int maxHpRetaliator) {
        this.resultProbability.setText(result.percentProbability() + "%");

        this.hpInitiator.setText(result.initiatorHp() + "/" + maxHpInitiator);
        this.hpRetaliator.setText(result.retaliatorHp() + "/" + maxHpRetaliator);
        this.simulationDetails.setListData(result.logs().stream()
                .map(BattleLog::toString)
                .toArray(String[]::new));
    }
}
