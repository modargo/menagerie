package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalignantTentacle extends CustomRelic {
    public static final String ID = "Menagerie:MalignantTentacle";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int CONSTRICTED = 1;
    private static final int TURNS = 3;

    private static final Map<String, Integer> stats = new HashMap<>();
    private static final String CONSTRICTED_STAT = "constricted";

    public MalignantTentacle() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], TURNS, CONSTRICTED);
    }

    @Override
    public void atBattleStart() {
        this.counter = TURNS - 2;
    }

    @Override
    public void atTurnStart() {
        this.counter = (this.counter + 1) % TURNS;
        if (this.counter == 0) {
            incrementConstrictedStat();
            this.flash();
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ConstrictedPower(AbstractDungeon.player, AbstractDungeon.player, CONSTRICTED), CONSTRICTED));
        }
    }

    public AbstractRelic makeCopy() {
        return new MalignantTentacle();
    }

    public String getStatsDescription() {
        return MessageFormat.format(DESCRIPTIONS[1], stats.get(CONSTRICTED_STAT));
    }



    public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
        DecimalFormat format = new DecimalFormat("#.###");
        float constricted = stats.get(CONSTRICTED_STAT);
        String constrictedPerTurn = format.format(constricted / Math.max(totalTurns, 1));
        String constrictedPerCombat = format.format(constricted / Math.max(totalCombats, 1));
        return getStatsDescription() + MessageFormat.format(DESCRIPTIONS[2], constrictedPerTurn, constrictedPerCombat);
    }

    public void resetStats() {
        stats.put(CONSTRICTED_STAT, 0);
    }

    public JsonElement onSaveStats() {
        Gson gson = new Gson();
        List<Integer> statsToSave = new ArrayList<>();
        statsToSave.add(stats.get(CONSTRICTED_STAT));
        return gson.toJsonTree(statsToSave);
    }

    public void onLoadStats(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            stats.put(CONSTRICTED_STAT, jsonArray.get(0).getAsInt());
        } else {
            resetStats();
        }
    }

    public static void incrementConstrictedStat() {
        stats.put(CONSTRICTED_STAT, stats.getOrDefault(CONSTRICTED_STAT, 0) + CONSTRICTED);
    }
}
