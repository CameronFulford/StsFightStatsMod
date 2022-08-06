package testMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testMod.DefaultMod;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "nextRoomTransition",
        paramtypez = {SaveFile.class}
)
public class DungeonPatch {
    static Logger logger = LogManager.getLogger(DungeonPatch.class.getName());
    public static void Prefix(AbstractDungeon _instance, SaveFile saveFile) {
        logger.info("nextRoomTransition patch");
        DefaultMod.inBattle = false;
    }
}
