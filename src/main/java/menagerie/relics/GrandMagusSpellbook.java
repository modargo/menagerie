package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.actions.GrandMagusSpellbookAction;
import menagerie.cards.BloodySpellbook;
import menagerie.util.TextureLoader;

import java.text.MessageFormat;

public class GrandMagusSpellbook extends CustomRelic {
    public static final String ID = "Menagerie:GrandMagusSpellbook";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int TURNS_PER_SPELL = 3;

    public GrandMagusSpellbook() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0].replace("{0}", TURNS_PER_SPELL + "");
    }

    @Override
    public void atBattleStart() {
        this.counter = -1;
        this.addToBot(new MakeTempCardInDrawPileAction(new BloodySpellbook(), 1, true, true));
    }

    @Override
    public void atTurnStartPostDraw() {
        this.counter = (this.counter + 1) % TURNS_PER_SPELL;
        if (this.counter == 0) {
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new GrandMagusSpellbookAction());
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1;
    }

    public AbstractRelic makeCopy() {
        return new GrandMagusSpellbook();
    }
}
