package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuirionDryadPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:QuirionDryad";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private List<AbstractCard.CardColor> colors;

    public QuirionDryadPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.colors = new ArrayList<>();
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!colors.contains(card.color)) {
            colors.add(card.color);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new DexterityPower(this.owner, this.amount), this.amount));
            this.updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0].replace("{0}", this.amount + "").replace("{1}", this.getPlayedColors());
    }

    private String getPlayedColors() {
        return this.colors.isEmpty()
                ? DESCRIPTIONS[1]
                : this.colors.stream().map(this::getColorName).collect(Collectors.joining(", "));
    }

    private String getColorName(AbstractCard.CardColor color) {
        switch (color) {
            case RED:
                return DESCRIPTIONS[2];
            case GREEN:
                return DESCRIPTIONS[3];
            case BLUE:
                return DESCRIPTIONS[4];
            case PURPLE:
                return DESCRIPTIONS[5];
            case COLORLESS:
                return DESCRIPTIONS[6];
            case CURSE:
                return DESCRIPTIONS[7];
            default:
                return color.name();
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

