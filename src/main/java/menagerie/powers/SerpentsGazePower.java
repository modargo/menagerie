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
    private static int CARDS = 1;
    private boolean active = true;

    public SerpentsGazePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], CARDS);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (this.active) {
            this.flash();
            if (card.cost >= 0) {
                card.setCostForTurn(Math.max(card.cost, card.costForTurn) + 1);
            }
            this.active = false;
        }
    }

    @Override
    public void atStartOfTurn() {
        this.active = true;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

