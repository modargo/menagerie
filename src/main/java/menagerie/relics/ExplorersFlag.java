package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExplorersFlag extends CustomRelic {
    public static final String ID = "Menagerie:ExplorersFlag";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int HEAL_AMOUNT = 7;

    private static final Map<String, Integer> stats = new HashMap<>();
    private static final String HEAL_STAT = "heal";

    public ExplorersFlag() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room.getMapSymbol() != null && room.getMapSymbol().equals("?")) {
            incrementHealStat();
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(HEAL_AMOUNT);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0].replace("{0}", HEAL_AMOUNT + "");
    }

    public AbstractRelic makeCopy() {
        return new ExplorersFlag();
    }

    public String getStatsDescription() {
        return MessageFormat.format(DESCRIPTIONS[1], stats.get(HEAL_STAT));
    }

    public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
        return getStatsDescription();
    }

    public void resetStats() {
        stats.put(HEAL_STAT, 0);
    }

    public JsonElement onSaveStats() {
        Gson gson = new Gson();
        List<Integer> statsToSave = new ArrayList<>();
        statsToSave.add(stats.get(HEAL_STAT));
        return gson.toJsonTree(statsToSave);
    }

    public void onLoadStats(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            stats.put(HEAL_STAT, jsonArray.get(0).getAsInt());
        } else {
            resetStats();
        }
    }

    public static void incrementHealStat() {
        stats.put(HEAL_STAT, stats.getOrDefault(HEAL_STAT, 0) + HEAL_AMOUNT);
    }
}
