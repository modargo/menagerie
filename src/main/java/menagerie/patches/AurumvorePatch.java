package menagerie.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import menagerie.relics.Aurumvore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "gainGold",
        paramtypez = int.class
)
public class AurumvorePatch {
    public static final Logger logger = LogManager.getLogger(AurumvorePatch.class.getName());

    @SpireInsertPatch(locator = AurumvorePatch.Locator.class)
    public static void ApplyAurumvore(AbstractPlayer __instance, @ByRef int[] amount) {
        if (AbstractDungeon.player.hasRelic(Aurumvore.ID)) {
            int goldEaten = (amount[0] + 1) / 2;
            amount[0] -= goldEaten;
            AbstractDungeon.player.getRelic(Aurumvore.ID).counter += goldEaten;
            logger.info("Aurumvore ate " + goldEaten + " gold");
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "goldGained");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}