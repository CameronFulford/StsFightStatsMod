package battleStats.ui;

import battleStats.model.EnemyCombatStats;
import battleStats.model.FightTracker;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class StatsRenderer {
    private static final Color TEXT_COLOR = Color.WHITE.cpy();
    private static final float POPIN_DURATION_SECONDS = 2f;

    private float popinTimer = POPIN_DURATION_SECONDS;

    public void render(SpriteBatch spriteBatch, EnemyCombatStats stats, FightTracker fightTracker) {
        renderTextPopip();

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
        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, aggregateStatsDisplay, 10, 900, TEXT_COLOR);

        // TODO: can render multi-line text with different fonts by offsetting text height. See TipHelper#getPowerTipHeight
        String currentFightStats = String.format("%s\n\nTurns: %d\nDamage taken: %d\nDamage dealt: %d", fightTracker.combatKey,
                fightTracker.numTurns, fightTracker.damageTaken, fightTracker.damageDealt);

        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, currentFightStats, 10, 700, TEXT_COLOR);
    }

    public void startPopInAnimation() {
        popinTimer = POPIN_DURATION_SECONDS;
    }

    private void renderTextPopip() {
        popinTimer -= Gdx.graphics.getDeltaTime();
        if (popinTimer < 0) {
            popinTimer = 0;
        }
        TEXT_COLOR.a = Interpolation.fade.apply(0.0F, 1.0F, 1.0F - popinTimer / POPIN_DURATION_SECONDS);
    }
}
