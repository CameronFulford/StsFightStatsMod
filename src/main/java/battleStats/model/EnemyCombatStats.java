package battleStats.model;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class models the stats for a combat vs a single enemy. It contains the summary of statistics from combats for the
 * enemy.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class EnemyCombatStats {
    private static final Logger logger = LogManager.getLogger(EnemyCombatStats.class.getName());
    public String combatEnemyKey;
    public int numCombats;
    public int wins;
    public int loss;
    public float averageDamageTaken;
    public float averageDamageDealt;
    public float averageTurnsToWin;

    public void addFightStats(FightTracker fightTracker) {
        logger.info("Adding fightTracker stats to EnemyCombatStats: " + this);
        if (fightTracker.result == FightTracker.FightResult.UNKNOWN) {
            logger.warn("Unable to add stats, invalid fight result: " + fightTracker.result);
            return;
        }

        if (fightTracker.result == FightTracker.FightResult.WIN) {
            averageTurnsToWin = computeNewAverage(averageTurnsToWin, wins, fightTracker.numTurns);
            wins++;
        } else if (fightTracker.result == FightTracker.FightResult.LOSS) {
            loss++;
        }

        averageDamageDealt = computeNewAverage(averageDamageDealt, numCombats, fightTracker.damageDealt);
        averageDamageTaken = computeNewAverage(averageDamageTaken, numCombats, fightTracker.damageTaken);
        numCombats++;

        logger.info("Updated EnemyCombatStats: " + this);
    }

    public static EnemyCombatStats fromFightTracker(FightTracker fightTracker) {
        return EnemyCombatStats.builder()
                .combatEnemyKey(fightTracker.combatKey)
                .numCombats(1)
                .wins((fightTracker.result == FightTracker.FightResult.WIN) ? 1 : 0)
                .loss((fightTracker.result == FightTracker.FightResult.LOSS) ? 1 : 0)
                .averageTurnsToWin((fightTracker.result == FightTracker.FightResult.WIN) ? fightTracker.numTurns : 0)
                .averageDamageTaken(fightTracker.damageTaken)
                .averageDamageDealt(fightTracker.damageDealt)
                .build();
    }

    /**
     * Computes a new average value given the current average plus one new data point.
     *
     * @param average the original average value
     * @param numValues the original number of values used in the original average
     * @param valueToAdd the new value that is being added
     * @return the resulting average when the new value has been incorporated
     */
    private float computeNewAverage(float average, int numValues, float valueToAdd) {
        return average * numValues / (numValues + 1f) + valueToAdd / (numValues + 1f);
    }
}
