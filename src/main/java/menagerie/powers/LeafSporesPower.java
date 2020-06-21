package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class LeafSporesPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:LeafSpores";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public LeafSporesPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onDeath() {
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            CardCrawlGame.sound.play("SPORE_CLOUD_RELEASE");
            this.flashWithoutSound();
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, null, new WeakPower(AbstractDungeon.player, this.amount, true), this.amount));
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, null, new FrailPower(AbstractDungeon.player, this.amount, true), this.amount));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

