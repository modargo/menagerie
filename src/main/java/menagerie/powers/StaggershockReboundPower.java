package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;
import menagerie.cards.spells.Staggershock;

public class StaggershockReboundPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:StaggershockRebound";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private Staggershock staggershock;

    public StaggershockReboundPower(AbstractCreature owner, Staggershock staggershock) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.staggershock = (Staggershock)staggershock.makeSameInstanceOf();
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        this.staggershock.purgeOnUse = true;
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(this.staggershock, null, this.staggershock.energyOnUse, true, true), true);
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}