package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;

public class SolarChargePower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:SolarCharge";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final int CHARGES_FOR_FLARE = 6;
    private static final int DAMAGE_PER_CHARGE = 10;

    private int damageCounter;

    public SolarChargePower(AbstractCreature owner, int amount) {
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
        this.description = DESCRIPTIONS[0].replace("{0}", DAMAGE_PER_CHARGE + "").replace("{1}", CHARGES_FOR_FLARE + "");
    }

    @Override
    public void atStartOfTurn() {
        this.damageCounter = 0;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount <= 0 && info.output > 0) {
            this.flash();
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new SolarChargePower(this.owner, 1), 1));
        }
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        this.damageCounter += damageAmount;
        if (this.damageCounter >= DAMAGE_PER_CHARGE) {
            int charges = this.damageCounter / DAMAGE_PER_CHARGE;
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new SolarChargePower(this.owner, charges), charges));
            this.damageCounter = this.damageCounter % DAMAGE_PER_CHARGE;
        }
        return damageAmount;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, stackAmount), stackAmount));
        super.stackPower(stackAmount);
    }

    @Override
    public void reducePower(int reduceAmount) {
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -reduceAmount), -reduceAmount));
        super.reducePower(reduceAmount);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

