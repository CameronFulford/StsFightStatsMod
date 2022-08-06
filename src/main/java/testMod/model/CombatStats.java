package testMod.model;

import java.util.Map;

public class CombatStats {
    // A map of enemy key to a map of combat enemy stats per character.
    public Map<String, Map<String, EnemyCombatStats>> enemyCombatStatsMap;

    public EnemyCombatStats getAggregateCombatStats(final String enemy) {
        if (enemyCombatStatsMap.containsKey(enemy)) {
            EnemyCombatStats aggregateStats = new EnemyCombatStats();
            for (EnemyCombatStats stats : enemyCombatStatsMap.get(enemy).values()) {
                aggregateStats = addStatsToAggregate(aggregateStats, stats);
            }

            return aggregateStats;
        }
        return null;
    }

    static EnemyCombatStats addStatsToAggregate(EnemyCombatStats aggregate, EnemyCombatStats stats) {
        aggregate.averageDamageDealt = combineAverageValues(aggregate.averageDamageDealt, aggregate.numCombats,
                stats.averageDamageDealt, stats.numCombats);
        aggregate.averageDamageTaken = combineAverageValues(aggregate.averageDamageTaken, aggregate.numCombats,
                stats.averageDamageTaken, stats.numCombats);
        aggregate.averageTurnsToWin = combineAverageValues(aggregate.averageTurnsToWin, aggregate.wins,
                stats.averageTurnsToWin, stats.wins);
        aggregate.loss += stats.loss;
        aggregate.wins += stats.wins;
        aggregate.numCombats += stats.numCombats;
        return aggregate;
    }

    private static float combineAverageValues(float average1, int n1, float average2, int n2) {
        return average1*(n1*1f)/(n1 + n2) + average2*(n2*1f)/(n1+n2);
    }
}
