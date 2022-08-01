package menagerie.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aurumvore extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = "Menagerie:Aurumvore";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int GOLD_PER_STRENGTH = 125;

    private static final Map<String, Integer> stats = new HashMap<>();
    private static final String GOLD_STAT = "gold";

    public Aurumvore() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], GOLD_PER_STRENGTH);
    }

    @Override
    public void onEquip() {
        int goldEaten = (AbstractDungeon.player.gold + 1) / 2;
        incrementGoldStat(goldEaten);
        AbstractDungeon.player.loseGold(goldEaten);
        this.counter = goldEaten;
    }

    @Override
    public void atBattleStart() {
        int strength = this.counter / GOLD_PER_STRENGTH;
        if (strength > 0) {
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, strength), strength));
        }
    }

    public AbstractRelic makeCopy() {
        return new Aurumvore();
    }

    @Override
    public Integer onSave() {
        return this.counter;
    }

    @Override
    public void onLoad(Integer counter) {
        this.counter = counter;
    }

    public String getStatsDescription() {
        return MessageFormat.format(DESCRIPTIONS[1], stats.get(GOLD_STAT));
    }

    public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
        return getStatsDescription();
    }

    public void resetStats() {
        stats.put(GOLD_STAT, 0);
    }

    public JsonElement onSaveStats() {
        Gson gson = new Gson();
        List<Integer> statsToSave = new ArrayList<>();
        statsToSave.add(stats.get(GOLD_STAT));
        return gson.toJsonTree(statsToSave);
    }

    public void onLoadStats(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            stats.put(GOLD_STAT, jsonArray.get(0).getAsInt());
        } else {
            resetStats();
        }
    }

    public static void incrementGoldStat(int amount) {
        stats.put(GOLD_STAT, stats.getOrDefault(GOLD_STAT, 0) + amount);
    }
}
