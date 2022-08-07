package testMod.model;

import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testMod.DefaultMod;

@ToString
public class FightTracker {
    public static final Logger logger = LogManager.getLogger(DefaultMod.class.getName());

    public String combatKey;
    public int numTurns;
    public int damageTaken;
    public int damageDealt;

    public FightResult result = FightResult.UNKNOWN;
    public enum FightResult {
        WIN,
        LOSS,
        SMOKED,
        UNKNOWN
    }
}
