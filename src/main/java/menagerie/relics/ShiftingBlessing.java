package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

public class ShiftingBlessing extends CustomRelic {
    public static final String ID = "Menagerie:ShiftingBlessing";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int STAT = 1;

    public ShiftingBlessing() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        this.counter = 1 - this.counter;
        this.flash();
        if (this.counter == 1) {
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, STAT), STAT));
            AbstractPower temporaryPower = new LoseStrengthPower(AbstractDungeon.player, STAT);
            temporaryPower.type = AbstractPower.PowerType.BUFF;
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, temporaryPower, STAT));
        }
        else {
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, STAT), STAT));
            AbstractPower temporaryPower = new LoseDexterityPower(AbstractDungeon.player, STAT);
            temporaryPower.type = AbstractPower.PowerType.BUFF;
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, temporaryPower, STAT));
        }
    }

    public AbstractRelic makeCopy() {
        return new ShiftingBlessing();
    }
}
