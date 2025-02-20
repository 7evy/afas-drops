package gui.frame;

import gui.panel.BattleCharacterPanel;
import gui.panel.BattleForecastPanel;

import javax.swing.JFrame;
import java.awt.Dimension;

public class BattleSimulatorFrame extends JFrame {
    private final BattleCharacterPanel characterPanelLeft;
    private final BattleCharacterPanel characterPanelRight;
    private final BattleForecastPanel battleForecastPanel;

    public BattleSimulatorFrame() {
        super();
        setPreferredSize(new Dimension(100, 100));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.characterPanelLeft = new BattleCharacterPanel();
        this.characterPanelRight = new BattleCharacterPanel();
        this.battleForecastPanel = new BattleForecastPanel();

        pack();
    }
}
