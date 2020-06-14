package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class HydraHeadsPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:HydraHeads";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int threshold;

    public HydraHeadsPower(AbstractCreature owner, int threshold) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.threshold = threshold;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.threshold);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount >= this.threshold && this.owner.hasPower(StrengthPower.POWER_ID) && this.owner.getPower(StrengthPower.POWER_ID).amount > 0) {
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -1), -1));
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new HydraHeadRegrowthPower(this.owner, 1), 1));
        }
        return damageAmount;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

