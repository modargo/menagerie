package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.actions.UpdatePowerDescriptionAction;

public class LoseStrengthNonDebuffPower extends LoseStrengthPower {
    public LoseStrengthNonDebuffPower(AbstractCreature owner, int newAmount) {
        super(owner, newAmount);
        this.type = null;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.flash();
        AbstractPower power = new StrengthPower(this.owner, -this.amount);
        power.type = null;
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, power, -this.amount));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, "Flex"));
        this.addToBot(new UpdatePowerDescriptionAction(power));
    }
}
