package testMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testMod.DefaultMod;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage"
)
public class PlayerPatch {
    public static final Logger logger = LogManager.getLogger(PlayerPatch.class.getName());

    public static void Postfix(AbstractPlayer _instance, DamageInfo info) {
        logger.info(String.format("Player damage. player isDead: %s, last damage taken: %d",
                AbstractDungeon.player.isDead,
                AbstractDungeon.player.lastDamageTaken));
        DefaultMod.fightTracker.damageTaken += AbstractDungeon.player.lastDamageTaken;
    }
}
