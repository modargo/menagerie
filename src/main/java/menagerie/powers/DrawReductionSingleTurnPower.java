package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.text.MessageFormat;

public class DrawReductionSingleTurnPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:DrawReductionSingleTurn";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public DrawReductionSingleTurnPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("lessdraw");
        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.player.gameHandSize -= this.amount;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public void onRemove() {
        AbstractDungeon.player.gameHandSize += this.amount;
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
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
