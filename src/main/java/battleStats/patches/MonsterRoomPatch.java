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
            if (BattleStatsMod.shouldRenderStats()) {
                BattleStatsMod.renderStats(spriteBatch);
            }
        }
    }
}
