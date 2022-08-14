package battleStats.patches;

import battleStats.BattleStatsMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

@SpirePatch(
        clz = AbstractRoom.class,
        method = "render"
)
public class MonsterRoomPatch {

    public static void Prefix(AbstractRoom _instance, SpriteBatch spriteBatch) {
        if (_instance instanceof MonsterRoom) {
            // TODO: if we always render stats here, when entering a new MonsterRoom it momentarily displays the
            //  previous combat stats since the receiveOnBattleStart method sets the current fight details and
            //  refreshes the stats. Otherwise, if we use shouldRenderStats then the stats don't display when
            //  loading into a completed monster room.
            if (true) {//BattleStatsMod.shouldRenderStats()) {
                BattleStatsMod.renderStats(spriteBatch);
            }
        }
    }
}
