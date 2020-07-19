package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.text.MessageFormat;

// This class exists to fix two issues with the base game's DrawPower:
// (1) Fix the description for draw reduction, which reads "-1 less card"/"-2 less cards"
// (2) Fix stacking behavior, which doesn't work for -1 due to that value being treated specially
public class PermanentDrawReductionPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:PermanentDrawReduction";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public PermanentDrawReductionPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.type = PowerType.DEBUFF;
        this.loadRegion("draw2");

        this.isTurnBased = false;
        AbstractDungeon.player.gameHandSize -= amount;
    }

    public void onRemove() {
        AbstractDungeon.player.gameHandSize += this.amount;
    }

    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0F;
        this.amount -= reduceAmount;
        if (this.amount == 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
        } else {
            this.description = MessageFormat.format(DESCRIPTIONS[1], this.amount);
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

