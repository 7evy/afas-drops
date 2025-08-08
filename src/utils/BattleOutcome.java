package utils;

import java.util.ArrayList;
import java.util.List;

public record BattleOutcome(int initiatorHp, int retaliatorHp, float percentProbability, List<BattleLog> logs) {
    public BattleOutcome attackInitiator(int damageToRetaliator, int damageToInitiator, float probability, BattleLog newLog) {
        List<BattleLog> nextLogs = new ArrayList<>(logs);
        nextLogs.add(newLog);
        return new BattleOutcome(Math.max(0, initiatorHp - damage), retaliatorHp, probability * percentProbability / 100, nextLogs);
    }

    public BattleOutcome attackRetaliator(int damageToInitiator, int damageToRetaliator, float probability, BattleLog newLog) {
        List<BattleLog> nextLogs = new ArrayList<>(logs);
        nextLogs.add(newLog);
        return new BattleOutcome(initiatorHp, Math.max(0, retaliatorHp - damage), probability * percentProbability / 100, nextLogs);
    }
}
