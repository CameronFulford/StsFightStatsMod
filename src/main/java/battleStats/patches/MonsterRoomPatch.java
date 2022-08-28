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

    private static AbstractRoom currentRoom;

    public static void Postfix(AbstractRoom _instance, SpriteBatch spriteBatch) {
        // Check if this is a MonsterRoom or possibly another room that has monsters (e.g. EventRoom)
        // isBattleOver seems to work for loading into event rooms post-combat. The challenge is the fight stats won't
        // be properly initialized since on load the event room doesn't set the dungeon lastCombatMetricKey.
        // TODO: refactor, cleanup
        if (shouldRender(_instance)) {
            // TODO: if this runs prior to battle start or the fight stats have not been initialized, then it renders
            //  old or null stats.
            BattleStatsMod.renderStats(spriteBatch);
        }
    }

    private static boolean shouldRender(AbstractRoom _instance) {
        if ((_instance instanceof MonsterRoom) || _instance.phase == AbstractRoom.RoomPhase.COMBAT
                || _instance.isBattleOver) {
            if (_instance.phase == AbstractRoom.RoomPhase.COMBAT || _instance.phase == AbstractRoom.RoomPhase.COMPLETE) {
                // Update the fight tracker if this is a new MonsterRoom instance.
                if (!_instance.equals(currentRoom)) {
                    currentRoom = _instance;

                    BattleStatsMod.initializeFightStats();
                }

                return true;
            }
        }
        return false;
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "update"
    )
    private static class MonsterRoomUpdatePatch {
        public static void Postfix(AbstractRoom _instance) {
            if (shouldRender(_instance)) {
                BattleStatsMod.updateStatsRenderer();
            }
        }
    }
}
