package model;

public record Support(SupportRank rank, Affinity affinity1, Affinity affinity2) {

    public SupportBonus doubledBonus() {
        return new SupportBonus(
                rank.ordinal() * (affinity1.bonus.hit + affinity2.bonus.hit),
                rank.ordinal() * (affinity1.bonus.avoid + affinity2.bonus.avoid),
                rank.ordinal() * (affinity1.bonus.crit + affinity2.bonus.crit),
                rank.ordinal() * (affinity1.bonus.dodge + affinity2.bonus.dodge),
                rank.ordinal() * (affinity1.bonus.damage + affinity2.bonus.damage),
                rank.ordinal() * (affinity1.bonus.protection + affinity2.bonus.protection)
        );
    }

    public static class SupportBonus {
        public final int hit;
        public final int avoid;
        public final int crit;
        public final int dodge;
        public final int damage;
        public final int protection;

        public SupportBonus(int hit, int avoid, int crit, int dodge, int damage, int protection) {
            this.hit = hit;
            this.avoid = avoid;
            this.crit = crit;
            this.dodge = dodge;
            this.damage = damage;
            this.protection = protection;
        }
    }
}
