package menagerie.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

import java.text.MessageFormat;

public class Aurumvore extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = "Menagerie:Aurumvore";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int GOLD_PER_STRENGTH = 150;

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
        AbstractDungeon.player.loseGold(goldEaten);
        this.counter = goldEaten;
    }

    @Override
    public void atBattleStart() {
        int strength = this.counter / GOLD_PER_STRENGTH;
        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, strength), strength));
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

}
