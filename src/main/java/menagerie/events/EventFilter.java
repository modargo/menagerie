package menagerie.events;

import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.AccursedBlacksmith;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import menagerie.act.MenagerieAct;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class EventFilter {
    private static final Logger logger = LogManager.getLogger(EventFilter.class.getName());

    //Not supposed to be instantiated
    private EventFilter() {
        throw new AssertionError();
    }

    public static ArrayList<String> FilterEvents(ArrayList<String> events) {
        ArrayList<String> eventsToRemove = new ArrayList<>();
        for (String event : events) {
            if (event.equals(StasisChamber.ID)) {
                if (!(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y > 6)) {
                    eventsToRemove.add(event);
                }
            }
            if (event.equals(MossyPath.ID)) {
                if (!(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y > 6)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(MagicPeddler.ID)) {
                int goldRequired = AbstractDungeon.ascensionLevel >= 15 ? MagicPeddler.A15_POTION_COST : MagicPeddler.POTION_COST;
                if (!(AbstractDungeon.player.gold >= goldRequired)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(GrandMagusTower.ID)) {
                if (!(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y > AbstractDungeon.map.size() / 2)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(ScentOfGold.ID)) {
                if (AbstractDungeon.player.hasRelic(Ectoplasm.ID)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(ShimmeringGrove.ID)) {
                if (ModHelper.isModEnabled(Diverse.ID) || AbstractDungeon.player.hasRelic(PrismaticShard.ID) || AbstractDungeon.eventRng.random(1.0F) < 0.5F) {
                    eventsToRemove.add(event);
                }
            }

            if (event.startsWith("Accursed_Blacksmith")) {
                if (AbstractDungeon.player.hasRelic(WarpedTongs.ID)) {
                    eventsToRemove.add(event);
                }
                else if (AbstractDungeon.id.equals(MenagerieAct.ID)) {
                    if (AbstractDungeon.eventRng.random(1.0F) < 0.5F) {
                        eventsToRemove.add(event);
                    }
                }
            }
        }
        return eventsToRemove;
    }
}