package battleStats.patches;

import battleStats.BattleStatsMod;
import battleStats.model.FightTracker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

@SpirePatch(
        clz = AbstractRoom.class,
        method = "render"
)
public class MonsterRoomPatch {

    private static AbstractRoom currentRoom;

    public static void Postfix(AbstractRoom _instance, SpriteBatch spriteBatch) {
        if (_instance instanceof MonsterRoom) {
            // Update the fight tracker if this is a new MonsterRoom instance.
            if (!_instance.equals(currentRoom)) {
                currentRoom = _instance;

                BattleStatsMod.initializeFightStats();
            }

            if (true) {//BattleStatsMod.shouldRenderStats()) {
                BattleStatsMod.renderStats(spriteBatch);
            }
        }
    }
}
