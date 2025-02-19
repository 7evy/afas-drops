package gui.frame;

import gui.panel.BattleCharacterPanel;
import gui.panel.BattleForecastPanel;

import javax.swing.*;

public class BattleSimulatorFrame extends JFrame {
    private final BattleCharacterPanel characterPanelLeft;
    private final BattleCharacterPanel characterPanelRight;
    private final BattleForecastPanel battleForecastPanel;

    public BattleSimulatorFrame() {
        this.characterPanelLeft = new BattleCharacterPanel();
        this.characterPanelRight = new BattleCharacterPanel();
        this.battleForecastPanel = new BattleForecastPanel();
    }
}
