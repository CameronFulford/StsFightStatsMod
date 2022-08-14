package battleStats.ui;

import battleStats.model.EnemyCombatStats;
import battleStats.model.FightTracker;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class StatsRenderer {

    public void render(SpriteBatch spriteBatch, EnemyCombatStats stats, FightTracker fightTracker) {
        final String aggregateStatsFormatString = "%s\n\nCombats (wins/losses/total): %d/%d/%d\nAvg damage taken: %.1f\n" +
                "Avg damage dealt: %.1f\nAverage # of turns to win: %.1f";
        String aggregateStatsDisplay;
        if (stats != null) {
            aggregateStatsDisplay = String.format(aggregateStatsFormatString, stats.combatEnemyKey,
                    stats.wins, stats.loss, stats.numCombats, stats.averageDamageTaken,
                    stats.averageDamageDealt, stats.averageTurnsToWin);
        } else {
            final int defaultIntValue = 0;
            final float defaultFloatValue = 0f;
            aggregateStatsDisplay = String.format(aggregateStatsFormatString, fightTracker.combatKey, defaultIntValue,
                    defaultIntValue, defaultIntValue, defaultFloatValue, defaultFloatValue, defaultFloatValue);
        }
        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, aggregateStatsDisplay, 10, 900, Color.WHITE);

        // TODO: can render multi-line text with different fonts by offsetting text height. See TipHelper#getPowerTipHeight
        String currentFightStats = String.format("%s\n\nTurns: %d\nDamage taken: %d\nDamage dealt: %d", fightTracker.combatKey,
                fightTracker.numTurns, fightTracker.damageTaken, fightTracker.damageDealt);
        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, currentFightStats, 10, 700, Color.WHITE);
    }
}
