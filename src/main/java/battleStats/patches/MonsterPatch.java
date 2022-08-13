package battleStats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import battleStats.BattleStatsMod;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "damage"
)
public class MonsterPatch {
    public static final Logger logger = LogManager.getLogger(MonsterPatch.class.getName());

    public static void Postfix(AbstractMonster _instance, DamageInfo info) {
        logger.info(String.format("Monster damage. monster isDead: %s, last damage taken: %d",
                _instance.isDead,
                _instance.lastDamageTaken));
        BattleStatsMod.fightTracker.damageDealt += _instance.lastDamageTaken;
    }
}
