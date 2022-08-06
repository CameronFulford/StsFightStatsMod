package testMod.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testMod.FightTracker;

/**
 * This class models the stats for a combat vs a single enemy. It contains the summary of statistics from combats for the
 * enemy.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnemyCombatStats {
    public String combatEnemyKey;
    public int numCombats;
    public int wins;
    public int loss;
    public float averageDamageTaken;
    public float averageDamageDealt;
    public float averageTurnsToWin;

    public void addFightStats(FightTracker fightTracker) {
        if (fightTracker.result == FightTracker.FightResult.UNKNOWN) {
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
