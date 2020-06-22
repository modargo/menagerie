package menagerie.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class UpdatePowerDescriptionAction extends AbstractGameAction {
    private AbstractPower power;

    public UpdatePowerDescriptionAction(AbstractPower power) {
        this.power = power;
        this.duration = 0.0F;
    }

    @Override
    public void update() {
        this.power.updateDescription();
        this.tickDuration();
    }
}
