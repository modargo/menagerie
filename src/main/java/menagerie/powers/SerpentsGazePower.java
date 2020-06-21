package menagerie.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class SerpentsGazePower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:SerpentsGaze";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int ADDITIONAL_COST = 1;
    private int counter;

    public SerpentsGazePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.counter = 0;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = this.amount == 1 ? MessageFormat.format(DESCRIPTIONS[0], ADDITIONAL_COST) : MessageFormat.format(DESCRIPTIONS[1], this.amount, ADDITIONAL_COST);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (this.counter > 0) {
            this.flash();
            if (card.cost >= 0) {
                card.setCostForTurn(Math.max(card.cost, card.costForTurn) + ADDITIONAL_COST);
            }
            this.counter--;
        }
    }

    @Override
    public void atStartOfTurn() {
        this.counter = this.amount;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

