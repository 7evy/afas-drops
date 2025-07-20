package model;

import java.util.function.Function;

public enum Skill {
    None(ignored -> 0),
    Astra(stats -> stats.skill / 2),
    Luna(stats -> stats.skill),
    Sol(stats -> stats.skill);

    public final Function<Stats, Integer> triggerRate;

    Skill(Function<Stats, Integer> triggerRate) {
        this.triggerRate = triggerRate;
    }
}
