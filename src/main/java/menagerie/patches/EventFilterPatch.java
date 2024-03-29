package menagerie.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import menagerie.events.EventFilter;
import javassist.CtBehavior;
import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getEvent",
        paramtypez = Random.class
)
// A patch to make certain events only appear if the player fulfills some condition
public class EventFilterPatch {
    @SpireInsertPatch(locator = EventFilterPatch.Locator.class, localvars = {"tmp"})
    public static void FilterEvent(Random rng, @ByRef ArrayList<String>[] tmp) {
        ArrayList<String> eventsToRemove = EventFilter.FilterEvents(tmp[0]);
        for (String event : eventsToRemove) {
            tmp[0].remove(event);
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}