package battleStats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import battleStats.BattleStatsMod;
import battleStats.model.FightTracker;

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
        BattleStatsMod.fightTracker.damageTaken += AbstractDungeon.player.lastDamageTaken;

        // Special handling if the player has died
        if (AbstractDungeon.player.isDead) {
            BattleStatsMod.fightTracker.result = FightTracker.FightResult.LOSS;
            BattleStatsMod.writeFightStatsToStore();
        }
    }
}
