package battleStats.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

/**
 * Renders the fight stats as a PowerTip when displaying character PowerTips.
 * TODO: fix to use the AbstractPlayer renderPowerTips method instead of the AbstractRelic renderTip method.
 */
@SpirePatch(
        clz = AbstractRelic.class,
        method = "renderTip"
//        method = "renderPowerTips" // for AbstractPlayer
)
public class CharacterTipPatch {

    private static boolean addedTip = false;
    private static ArrayList<PowerTip> originalTips;

    public static boolean shouldShowStats() {
        return false; //CardCrawlGame.mode == CardCrawlGame.GameMode.GAMEPLAY && AbstractDungeon.player != null;
    }

    @SuppressWarnings("unchecked")
    public static void Prefix(AbstractRelic _instance, SpriteBatch sb) {
        if (shouldShowStats()) {
            addedTip = true;
            originalTips = _instance.tips;
            _instance.tips = (ArrayList<PowerTip>) _instance.tips.clone();
            _instance.tips.add(new PowerTip("Hello Griffin!", "Hello Edwin."));
        }
    }

    public static void Postfix(AbstractRelic _instance, SpriteBatch sb) {
        if (addedTip) {
            _instance.tips = originalTips;
            addedTip = false;
        }

    }
}

