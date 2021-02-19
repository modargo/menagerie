package menagerie.events;

import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import menagerie.act.MenagerieAct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EventFilter {
    //Not supposed to be instantiated
    private EventFilter() {
        throw new AssertionError();
    }

    public static ArrayList<String> FilterEvents(ArrayList<String> events) {
        ArrayList<String> eventsToRemove = new ArrayList<>();

        if (AbstractDungeon.id.equals(MenagerieAct.ID)) {
            //These events are rarer (occurring at half the likelihood of other events) because the Menagerie has a lot
            //of events, and we don't want the balance to be too out of whack. This keeps variety while avoiding too
            //many events with certain profiles.
            //Reasons for each event being here:
            //* Shimmering Grove: Prismatic Shard shouldn't come up too often, given how much it changes the run
            //* Cloud Vision: The event is quite good, and the event pool is already strong
            //* Lunar Gift/Silence: The events are (deliberately) very similar, damage for a relic
            //* At The Peak: The event was the last added curse for benefit event and there's multiple others
            //Note that below Accursed Blacksmith is also reduced frequency, to mimic the way shrines normally work
            Set<String> reducedFrequencyEvents = new HashSet<>();
            reducedFrequencyEvents.add(ShimmeringGrove.ID);
            reducedFrequencyEvents.add(CloudVision.ID);
            reducedFrequencyEvents.add(LunarGift.ID);
            reducedFrequencyEvents.add(Silence.ID);
            reducedFrequencyEvents.add(AtThePeak.ID);
            reducedFrequencyEvents.retainAll(events);
            if (!reducedFrequencyEvents.isEmpty() && AbstractDungeon.eventRng.random(1.0F) < 0.5F) {
                eventsToRemove.addAll(reducedFrequencyEvents);
            }
        }

        for (String event : events) {
            if (eventsToRemove.contains(event)) {
                continue;
            }

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
                if (ModHelper.isModEnabled(Diverse.ID) || AbstractDungeon.player.hasRelic(PrismaticShard.ID)) {
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