package battleStats.patches;

import battleStats.BattleStatsMod;
import battleStats.model.FightTracker;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = ConfirmPopup.class,
        method = "yesButtonEffect"
)
public class ConfirmPopupPatch {
    public static final Logger logger = LogManager.getLogger(ConfirmPopupPatch.class.getName());

    public static void Postfix(ConfirmPopup _instance) {
        logger.info("ConfirmPopup Yes for type: " + _instance.type);
        if (_instance.type == ConfirmPopup.ConfirmType.ABANDON_MAIN_MENU || _instance.type == ConfirmPopup.ConfirmType.ABANDON_MID_RUN) {
            BattleStatsMod.fightTracker.result = FightTracker.FightResult.LOSS;
            BattleStatsMod.writeFightStatsToStore();
        }
    }
}
