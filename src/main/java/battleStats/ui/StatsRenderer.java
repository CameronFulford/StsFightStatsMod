package battleStats.ui;

import battleStats.model.EnemyCombatStats;
import battleStats.model.FightTracker;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class StatsRenderer {
    // TODO: scale X,Y values. Align stats top to bottom of the top panel.
    private static final float START_Y = 900;
    private static final float LABEL_X = 10;
    private static final float VALUE_X = 400;

    private static final Color TEXT_COLOR = Color.WHITE.cpy();
    private static final float POPIN_DURATION_SECONDS = 2f;

    private float popinTimer = POPIN_DURATION_SECONDS;

    private List<StatLine> aggregateStatsLines;
    private List<StatLine> combatStatsLines;
    public StatsRenderer() {
        // TODO: handle null EnemyCombatStats. Add null check or alternate StatLines.
        aggregateStatsLines = Arrays.asList(
                new StatLine("Combats (wins/losses/total)", (ecs, ft) -> String.format("%d/%d/%d", ecs.wins, ecs.loss, ecs.numCombats)),
                new StatLine("Avg damage taken", (ecs, ft) -> String.format("%.1f", ecs.averageDamageTaken)),
                new StatLine("Avg damage dealt", (ecs, ft) -> String.format("%.1f", ecs.averageDamageDealt)),
                new StatLine("Avg # of turns to win", (ecs, ft) -> String.format("%.1f", ecs.averageTurnsToWin))
        );
        combatStatsLines = Arrays.asList(
                new StatLine("Turns", (ecs, ft) -> String.format("%d", ft.numTurns)),
                new StatLine("Damage taken", (ecs, ft) -> String.format("%d", ft.damageTaken)),
                new StatLine("Damage dealt", (ecs, ft) -> "" + ft.damageDealt)
        );
    }

    public void render(SpriteBatch spriteBatch, EnemyCombatStats stats, FightTracker fightTracker) {
        renderTextPopip();
        // TODO: can we initialize the font on ctor? static initialize didn't work as the font was still null.
        BitmapFont font = FontHelper.tipBodyFont;
        float lineHeight = font.getLineHeight() * Settings.scale;

        // Note: general alignment implementation taken from GameOverScreen

        float y = START_Y;

        // Render VS line
        FontHelper.renderFontLeftTopAligned(spriteBatch, font, String.format("%s VS %s", AbstractDungeon.player.name, fightTracker.combatKey),
                LABEL_X, y, TEXT_COLOR);
        y -= lineHeight + 5f;

        // Render aggregate stats lines
        if (stats != null) {
            for (StatLine line : aggregateStatsLines) {
                line.renderLine(spriteBatch, stats, fightTracker, font, y);
                y -= lineHeight;
            }
        }

        // TODO: render separator between aggregate and current fight stats
        y -= 10f;

        // Render combat stats lines
        for (StatLine line : combatStatsLines) {
            line.renderLine(spriteBatch, stats, fightTracker, font, y);
            y -= lineHeight;
        }

        // Test
        FontHelper.renderFont(spriteBatch, font, String.format("scale %f, asc %f, desc %f, line height %f, x height %f", Settings.scale, font.getAscent(), font.getDescent(), font.getLineHeight(), font.getXHeight()), 10, 300, Color.WHITE);
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

    private static class StatLine {
        String label;
        BiFunction<EnemyCombatStats, FightTracker, String> valueFunction;
        Color valueColor;
        static final float HIGHLIGHT_TIME_SEC = 4f;
        float highlightTimer = HIGHLIGHT_TIME_SEC;
        String prevValue;

        StatLine(String label, BiFunction<EnemyCombatStats, FightTracker, String> valueFunction) {
            this.label = label;
            this.valueFunction = valueFunction;
            this.valueColor = Color.WHITE.cpy();
        }

        void renderLine(SpriteBatch spriteBatch, EnemyCombatStats enemyCombatStats, FightTracker fightTracker, BitmapFont font, float y) {
            FontHelper.renderFontLeftTopAligned(spriteBatch, font, label, LABEL_X, y, TEXT_COLOR);

            updateHighlight();
            String value = valueFunction.apply(enemyCombatStats, fightTracker);
            if (!value.equals(prevValue)) {
                startHighlight();
                prevValue = value;
            }
            FontHelper.renderFontRightTopAligned(spriteBatch, font, value, VALUE_X, y, valueColor);
        }

        void updateHighlight() {
            highlightTimer -= Gdx.graphics.getDeltaTime();
            if (highlightTimer < 0) {
                highlightTimer = 0;
            }
            valueColor.lerp(Color.WHITE, (HIGHLIGHT_TIME_SEC - highlightTimer)/HIGHLIGHT_TIME_SEC);
            // Copy the alpha from the general text color to get the same fade in effect.
            valueColor.a = TEXT_COLOR.a;
        }

        void startHighlight() {
            valueColor = Color.RED.cpy();
            highlightTimer = HIGHLIGHT_TIME_SEC;
        }
    }
}
