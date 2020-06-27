package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;

import java.text.MessageFormat;

public class CrumblingSanctuaryPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:CrumblingSanctuary";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public CrumblingSanctuaryPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        int modifiedDamage = damageAmount;
        int drawPileLeft = AbstractDungeon.player.drawPile.size();
        while (modifiedDamage > 0 && drawPileLeft > 0) {
            AbstractCard card = AbstractDungeon.player.drawPile.group.get(drawPileLeft - 1);
            this.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile, true));
            modifiedDamage = modifiedDamage - this.amount;
            drawPileLeft--;
        }

        return Math.max(modifiedDamage, 0);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

