package menagerie.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import menagerie.relics.Aurumvore;

import java.util.ArrayList;

public class EventFilter {
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
        }
        return eventsToRemove;
    }
}