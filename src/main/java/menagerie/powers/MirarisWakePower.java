package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;

public class MirarisWakePower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:MirarisWake";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public MirarisWakePower(AbstractCreature owner, int amount) {
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
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < this.amount; i++) {
            sb.append("[E] ");
        }
        String energy = sb.toString();

        this.description = DESCRIPTIONS[0].replace("{0}", energy);
    }

    @Override
    public void atStartOfTurn() {
        this.addToBot(new GainEnergyAction(this.amount));
        this.flash();
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

