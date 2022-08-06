package testMod.model;

import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class CombatStats {
    static Logger logger = LogManager.getLogger(CombatStats.class.getName());

    // A map of enemy key to a map of combat enemy stats per character.
    public Map<String, Map<String, EnemyCombatStats>> enemyCombatStatsMap = new HashMap<>();

    public void addCombatStats(String enemyKey, String character, EnemyCombatStats stats) {
        logger.info("Adding stats for enemy " + enemyKey + ", character " + character + ": " + stats);
        if (!enemyCombatStatsMap.containsKey(enemyKey)) {
            enemyCombatStatsMap.put(enemyKey, new HashMap<>());
        }

        Map<String, EnemyCombatStats> characterStatsMap = enemyCombatStatsMap.get(enemyKey);
        if (!characterStatsMap.containsKey(character)) {
            characterStatsMap.put(character, null);
        }

        // Add the new stats to the character's current stats.
        EnemyCombatStats characterStats = characterStatsMap.get(character);
        characterStatsMap.put(character, addStatsToAggregate(characterStats, stats));
    }

    public EnemyCombatStats getAggregateCombatStats(final String enemy) {
        logger.info("Getting aggregate stats for encounter " + enemy);
        if (enemyCombatStatsMap.containsKey(enemy)) {
            EnemyCombatStats aggregateStats = EnemyCombatStats.builder()
                    .combatEnemyKey(enemy)
                    .build();
            for (EnemyCombatStats stats : enemyCombatStatsMap.get(enemy).values()) {
                aggregateStats = addStatsToAggregate(aggregateStats, stats);
            }

            return aggregateStats;
        }
        return null;
    }

    static EnemyCombatStats addStatsToAggregate(EnemyCombatStats aggregate, EnemyCombatStats stats) {
        if (aggregate == null) {
            return stats.toBuilder().build();
        }
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
