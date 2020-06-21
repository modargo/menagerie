package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import menagerie.Menagerie;
import menagerie.actions.GrandMagusSpellbookAction;
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
        return MessageFormat.format(DESCRIPTIONS[0], TURNS_PER_SPELL);
    }

    @Override
    public void atBattleStart() {
        this.counter = -1;
    }

    @Override
    public void atTurnStart() {
        this.counter = (this.counter + 1) % TURNS_PER_SPELL;
        if (this.counter == 0) {
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new GrandMagusSpellbookAction());
        }
    }

    public AbstractRelic makeCopy() {
        return new GrandMagusSpellbook();
    }
}
