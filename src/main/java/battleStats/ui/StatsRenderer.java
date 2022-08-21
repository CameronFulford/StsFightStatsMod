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
import java.util.Random;
import java.util.function.BiFunction;

public class StatsRenderer {
    // TODO: scale X,Y values. Align stats top to bottom of the top panel.
    private static final float START_Y = 900;
    private static final float LABEL_X = 10;
    private static final float VALUE_X = 400;
    private static final float CENTER_X = (VALUE_X + LABEL_X) / 2f;

    private static final Color TEXT_COLOR = Color.WHITE.cpy();
    private static final float POPIN_DURATION_SECONDS = 2f;

    private float popinTimer = POPIN_DURATION_SECONDS;

    private List<StatLine> aggregateStatsLines;
    private List<StatLine> combatStatsLines;
    public StatsRenderer() {
        // TODO: handle null EnemyCombatStats. Add null check or alternate StatLines.
        aggregateStatsLines = Arrays.asList(
                new StatLine("Combats (wins/losses/total)",
                        (ecs, ft) -> ecs != null ? String.format("%d/%d/%d", ecs.wins, ecs.loss, ecs.numCombats) : "-/-/-", Color.GREEN),
                new StatLine("Avg damage taken",
                        (ecs, ft) -> ecs != null ? String.format("%.1f", ecs.averageDamageTaken) : "-", Color.GREEN),
                new StatLine("Avg damage dealt",
                        (ecs, ft) -> ecs != null ? String.format("%.1f", ecs.averageDamageDealt) : "-", Color.GREEN),
                new StatLine("Avg # of turns to win",
                        (ecs, ft) -> ecs != null ? String.format("%.1f", ecs.averageTurnsToWin) : "-", Color.GREEN)
        );
        combatStatsLines = Arrays.asList(
                new StatLine("Turns", (ecs, ft) -> String.format("%d", ft.numTurns), Color.SKY),
                new StatLine("Damage taken", (ecs, ft) -> String.format("%d", ft.damageTaken), true),
                new StatLine("Damage dealt", (ecs, ft) -> "" + ft.damageDealt, true)
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
        FontHelper.renderFontCentered(spriteBatch, font, String.format("%s VS %s", AbstractDungeon.player.name, fightTracker.combatKey),
                CENTER_X, y, TEXT_COLOR);
        y -= lineHeight + 5f;

        // Render aggregate stats lines
        for (StatLine line : aggregateStatsLines) {
            line.renderLine(spriteBatch, stats, fightTracker, font, y);
            y -= lineHeight;
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
        final Color highlightColor;
        static final float HIGHLIGHT_TIME_SEC = 2f;
        float highlightTimer = HIGHLIGHT_TIME_SEC;
        String prevValue;
        final boolean shakeValue;
        static final float SHAKE_TIME_SEC = 0.5f;
        static final float SHAKE_OFFSET_RANGE = 5f;
        float xOffset = 0;
        float yOffset = 0;
        Random random = new Random();

        StatLine(String label, BiFunction<EnemyCombatStats, FightTracker, String> valueFunction) {
            this(label, valueFunction, false, Color.RED);
        }
        StatLine(String label, BiFunction<EnemyCombatStats, FightTracker, String> valueFunction, Color highlightColor) {
            this(label, valueFunction, false, highlightColor);
        }
        StatLine(String label, BiFunction<EnemyCombatStats, FightTracker, String> valueFunction, boolean shakeValue) {
            this(label, valueFunction, shakeValue, Color.RED);
        }
        StatLine(String label, BiFunction<EnemyCombatStats, FightTracker, String> valueFunction, boolean shakeValue, Color highlightColor) {
            this.label = label;
            this.valueFunction = valueFunction;
            this.valueColor = Color.WHITE.cpy();
            this.shakeValue = shakeValue;
            this.highlightColor = highlightColor;
        }

        void renderLine(SpriteBatch spriteBatch, EnemyCombatStats enemyCombatStats, FightTracker fightTracker, BitmapFont font, float y) {
            FontHelper.renderFontLeftTopAligned(spriteBatch, font, label, LABEL_X, y, TEXT_COLOR);

            updateHighlight();
            String value = valueFunction.apply(enemyCombatStats, fightTracker);
            if (!value.equals(prevValue)) {
                startHighlight();
                prevValue = value;
            }
            FontHelper.renderFontRightTopAligned(spriteBatch, font, value, VALUE_X + xOffset, y + yOffset, valueColor);
        }

        void updateHighlight() {
            if (highlightTimer > 0) {
                highlightTimer -= Gdx.graphics.getDeltaTime();
                valueColor.set(highlightColor);
                if (highlightTimer < 0) {
                    highlightTimer = 0;
                }
                valueColor.lerp(Color.WHITE, (HIGHLIGHT_TIME_SEC - highlightTimer) / HIGHLIGHT_TIME_SEC);
            }

            if (shakeValue) {
                float shakeTimer = highlightTimer - HIGHLIGHT_TIME_SEC + SHAKE_TIME_SEC;
                if (shakeTimer <= 0) {
                    xOffset = 0;
                    yOffset = 0;
                } else {
                    // Update xOffset and yOffset to make the value "shake"
                    float rand1 = random.nextFloat() - 0.5f;
                    float rand2 = random.nextFloat() - 0.5f;
                    xOffset = 2 * rand1 * SHAKE_OFFSET_RANGE * shakeTimer / SHAKE_TIME_SEC;
                    yOffset = 2 * rand2 * SHAKE_OFFSET_RANGE * shakeTimer / SHAKE_TIME_SEC;
                }
            }

            // Copy the alpha from the general text color to get the same fade in effect.
            valueColor.a = TEXT_COLOR.a;
        }

        void startHighlight() {
            highlightTimer = HIGHLIGHT_TIME_SEC;
        }
    }
}
