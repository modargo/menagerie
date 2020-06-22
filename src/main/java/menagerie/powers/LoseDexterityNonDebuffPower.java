package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import menagerie.actions.UpdatePowerDescriptionAction;

public class LoseDexterityNonDebuffPower extends LoseDexterityPower {
    public LoseDexterityNonDebuffPower(AbstractCreature owner, int newAmount) {
        super(owner, newAmount);
        this.type = null;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.flash();
        AbstractPower power = new DexterityPower(this.owner, -this.amount);
        power.type = null;
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, power, -this.amount));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, "DexLoss"));
        this.addToBot(new UpdatePowerDescriptionAction(power));
    }
}
