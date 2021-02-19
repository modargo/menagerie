package menagerie.patches;

import actlikeit.savefields.BreadCrumbs;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import javassist.CtBehavior;
import menagerie.act.Encounters;
import menagerie.act.MenagerieAct;
import menagerie.monsters.bosses.Chimera;
import menagerie.monsters.bosses.Suneater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpirePatch(
        clz = MindBloom.class,
        method = "buttonEffect",
        paramtypez = int.class
)
public class MindBloomPatch {
    @SpireInsertPatch(locator = MindBloomPatch.Locator.class, localvars = {"list"})
    public static void FilterEvent(MindBloom __instance, int buttonPressed, @ByRef ArrayList<String>[] listByRef) {
        Map<Integer, String> breadCrumbs = BreadCrumbs.getBreadCrumbs();
        if (breadCrumbs != null && breadCrumbs.containsKey(MenagerieAct.ACT_NUM) && breadCrumbs.get(MenagerieAct.ACT_NUM).equals(MenagerieAct.ID)) {
            List<String> list = listByRef[0];
            list.clear();
            list.add(Chimera.ID);
            list.add(Suneater.ID);
            list.add(Encounters.AVATARS);
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}