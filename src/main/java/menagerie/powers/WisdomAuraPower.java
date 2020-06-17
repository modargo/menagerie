package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class WisdomAuraPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:WisdomAura";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int DRAW_AMOUNT = 2;
    private static final int FRAIL_AMOUNT = 1;

    public WisdomAuraPower(AbstractCreature owner, int amount) {
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
        this.description = MessageFormat.format(DESCRIPTIONS[0], DRAW_AMOUNT, FRAIL_AMOUNT, this.amount);
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this.owner, new WisdomPower(AbstractDungeon.player)));
    }

    @Override
    public void atEndOfRound() {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, this.owner, new FrailPower(AbstractDungeon.player, FRAIL_AMOUNT, true)));
    }

    @Override
    public void onDeath() {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(AbstractDungeon.player, this.owner, WisdomPower.POWER_ID));
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this.owner && !m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.owner, new ThornsPower(m, this.amount), this.amount));
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

