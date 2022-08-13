package battleStats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = AbstractRoom.class,
        method = "endBattle"
)
public class RoomPatch {
    public static final Logger logger = LogManager.getLogger(RoomPatch.class.getName());

    public static void Prefix(AbstractRoom _instance) {
        logger.info(String.format("endBattle. player isDead: %s, cards played: %d, damage received: %d",
                AbstractDungeon.player.isDead,
                AbstractDungeon.actionManager.cardsPlayedThisCombat.size(),
                GameActionManager.damageReceivedThisCombat));
    }
}
