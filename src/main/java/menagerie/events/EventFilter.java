package menagerie.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
                if (!(AbstractDungeon.currMapNode.y > 6)) {
                    eventsToRemove.add(event);
                }
            }
        }
        return eventsToRemove;
    }
}