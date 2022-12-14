package battleStats.ui;

import battleStats.model.EnemyCombatStats;
import battleStats.model.FightTracker;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class StatsRenderer {
    // TODO: scale X,Y values. Align stats top to bottom of the top panel.

    // TOP_Y value is based on the screen height minus desktop AbstractRelic Y draw position (centered at
    // Settings.HEIGHT - 102f and a relic image height of 128).
    // Note: although AbstractRelic seems to indicate the relic renders with a height of 128, in game it appears to be
    // much less. Not sure why the relic seems smaller.
    private static final float TOP_Y = Settings.HEIGHT - (102f + 64f);
    private static final float LABEL_X = 10;
    private static final float VALUE_X = 400;
    private static final float START_Y = TOP_Y;
    private static final float CENTER_X = (VALUE_X + LABEL_X) / 2f;

    // TODO: get actual screen width and Y limits to use here for offset limiting
    private static final float MIN_X_OFFSET = -CENTER_X;
    private static final float MAX_X_OFFSET = Settings.WIDTH - CENTER_X;
    private static final float MIN_Y_OFFSET = -START_Y + 200;
    private static final float MAX_Y_OFFSET = 200;

    private static final Color TEXT_COLOR = Color.WHITE.cpy();
    private static final float POPIN_DURATION_SECONDS = 1f;

    private float popinTimer = POPIN_DURATION_SECONDS;

    // Box constants
    private static final float SHADOW_DIST_X = 9.0F * 1f;//Settings.scale;
    private static final float SHADOW_DIST_Y = 14.0F * 1f;//Settings.scale;
    private static final float BOX_EDGE_H = 32.0F * 1f;//Settings.scale;
    private static final float BOX_BODY_H = 64.0F * 1f;//Settings.scale;
    private static final float BOX_W = 320.0F * 1f;//Settings.scale;

    private List<StatLine> aggregateStatsLines;
    private List<StatLine> combatStatsLines;

    private Hitbox hb = new Hitbox(0f, 0f);
    private GlyphLayout layout = new GlyphLayout();
    private boolean isClicked = false;
    private float xOffset = 0;
    private float yOffset = 0;
    private float dragX = 0f;
    private float dragY = 0f;

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

    public void update() {
        // Check for hitbox dragging
        hb.update();
        if (hb.hovered) {
            if (InputHelper.justClickedLeft) {
                dragX = InputHelper.mX - xOffset;
                dragY = InputHelper.mY - yOffset;
                isClicked = true;
            }
        }
        if (InputHelper.justReleasedClickLeft) {
            isClicked = false;
        }
        if (isClicked) {
            xOffset = Math.max(MIN_X_OFFSET, Math.min(MAX_X_OFFSET, InputHelper.mX - dragX));
            yOffset = Math.max(MIN_Y_OFFSET, Math.min(MAX_Y_OFFSET, InputHelper.mY - dragY));
        }
    }

    public void render(SpriteBatch spriteBatch, EnemyCombatStats stats, FightTracker fightTracker) {
        renderTextPopip();
        // TODO: can we initialize the font on ctor? static initialize didn't work as the font was still null.
        BitmapFont font = FontHelper.tipBodyFont;
        float lineHeight = font.getLineHeight() * Settings.scale;

        // Note: general alignment implementation taken from GameOverScreen

        float y = START_Y + yOffset;
        float labelX = LABEL_X + xOffset;
        float valueX = VALUE_X + xOffset;
        float centerX = CENTER_X + xOffset;
        float hbW = 0f;

        // Render VS line
        String vsLine = String.format("%s VS %s", AbstractDungeon.player.name, fightTracker.combatKey);
        FontHelper.renderFontCentered(spriteBatch, font, vsLine, centerX, y - lineHeight / 2f, TEXT_COLOR);
        y -= lineHeight * 1.5f + 5f;

        layout.setText(font, vsLine);
        hbW = Math.max(hbW, layout.width);


        // Render aggregate stats lines
        for (StatLine line : aggregateStatsLines) {
            line.renderLine(spriteBatch, stats, fightTracker, font, labelX, valueX, y);
            y -= lineHeight;
        }

        // TODO: render separator between aggregate and current fight stats
        y -= 10f;

        // Render combat stats lines
        for (StatLine line : combatStatsLines) {
            line.renderLine(spriteBatch, stats, fightTracker, font, labelX, valueX, y);
            y -= lineHeight;
        }

        // Test
        FontHelper.renderFont(spriteBatch, font,
                String.format("Settings.scale %f, TOP_Y %f, Settings.HEIGHT %d, InputHelper.mX %d, InputHelper.mY %d, dragX %f",
                        Settings.scale, TOP_Y, Settings.HEIGHT, InputHelper.mX, InputHelper.mY, dragX), 10, 400, Color.WHITE);
//        renderBox(500, 500, spriteBatch);

        // Resize and translate hitbox. Assumes static StatLine width.
        float statsLineWidth = VALUE_X - LABEL_X;
        hbW = Math.max(hbW, statsLineWidth);
        float hbH = START_Y + yOffset - y;
        layout.setText(font, vsLine);
        hb.resize(hbW, hbH);
        hb.translate(labelX, y);
        hb.render(spriteBatch);
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

        void renderLine(SpriteBatch spriteBatch, EnemyCombatStats enemyCombatStats, FightTracker fightTracker, BitmapFont font,
                        float labelX, float valueX, float y) {
            FontHelper.renderFontLeftTopAligned(spriteBatch, font, label, labelX, y, TEXT_COLOR);
            updateHighlight();
            String value = valueFunction.apply(enemyCombatStats, fightTracker);
            if (!value.equals(prevValue)) {
                startHighlight();
                prevValue = value;
            }
            FontHelper.renderFontRightTopAligned(spriteBatch, font, value, valueX + xOffset, y + yOffset, valueColor);
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

    private static void renderBox(float x, float y, SpriteBatch sb) {
        float h = 50f;


        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);


        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);


//        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);


//        FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, description, x + TEXT_OFFSET_X, y + BODY_OFFSET_Y, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
    }
}
