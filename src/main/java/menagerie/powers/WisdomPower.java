package menagerie.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class WisdomPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:Wisdom";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int DRAW_AMOUNT = 2;

    public WisdomPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.updateDescription();
        Menagerie.LoadPowerImage(this);
        AbstractDungeon.player.gameHandSize += DRAW_AMOUNT;
    }
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], DRAW_AMOUNT);
    }

    public void onRemove() {
        AbstractDungeon.player.gameHandSize -= DRAW_AMOUNT;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

