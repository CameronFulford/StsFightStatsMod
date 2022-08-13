package battleStats.stats;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import battleStats.model.CombatStats;
import battleStats.model.EnemyCombatStats;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StatsUtils {
    static Logger logger = LogManager.getLogger(StatsUtils.class.getName());
    public static CombatStats generateTestStats() {
        CombatStats combatStats = new CombatStats();
        List<String> enemies = Arrays.asList(
                MonsterHelper.TWO_LOUSE_ENC,
                MonsterHelper.CULTIST_ENC,
                MonsterHelper.JAW_WORM_ENC,
                MonsterHelper.SMALL_SLIMES_ENC
        );
        List<String> characters = Arrays.asList(
                TheSilent.class.getSimpleName(),
                Defect.class.getSimpleName(),
                Ironclad.class.getSimpleName(),
                Watcher.class.getSimpleName()
        );

        for (String enemy : enemies) {
            for (String character : characters) {
                logger.info("Generating dummy stats for enemy/character " + enemy + "/" + character);
                combatStats.addCombatStats(enemy, character, generateTestEnemyStats(enemy));
            }
        }
        return combatStats;
    }

    public static EnemyCombatStats generateTestEnemyStats(String enemyKey) {
        Random r = new Random();
        int loss = r.nextInt(5);
        int wins = r.nextInt(10);
        return EnemyCombatStats.builder()
                .loss(loss)
                .wins(wins)
                .combatEnemyKey(enemyKey)
                .averageDamageDealt(10 + 40 * r.nextFloat())
                .averageDamageTaken(10 + 40 * r.nextFloat())
                .averageTurnsToWin(1 + 10 * r.nextFloat())
                .numCombats(loss + wins)
                .build();
    }
}
