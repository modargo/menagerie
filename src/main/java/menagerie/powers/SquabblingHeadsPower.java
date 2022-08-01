package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MalleablePower;
import menagerie.Menagerie;
import menagerie.monsters.bosses.Chimera;

public class SquabblingHeadsPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:SquabblingHeads";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private final static int SWITCH_THRESHOLD = 3;
    private final static int MALLEABLE_AMOUNT = 2;
    private final static int SERPENTS_GAZE_AMOUNT = 1;
    private boolean lionActive;
    private boolean goatActive;
    private boolean snakeActive;

    public SquabblingHeadsPower(Chimera owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;
        this.lionActive = true;
        this.goatActive = false;
        this.snakeActive = false;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + " NL ";
        if (this.areAllActive()) {
            this.description += DESCRIPTIONS[1];
        }
        else if (this.lionActive) {
            this.description += DESCRIPTIONS[2];
        }
        else if (this.goatActive) {
            this.description += DESCRIPTIONS[3];
        }
        else if (this.snakeActive) {
            this.description += DESCRIPTIONS[4];
        }
    }

    public void switchToNextHead() {
        this.flash();
        this.amount = 0;
        if (this.lionActive) {
            this.setLionActive(false);
            this.setGoatActive(true);
        }
        else if (this.goatActive) {
            this.setGoatActive(false);
            this.setSnakeActive(true);
        }
        else if (this.snakeActive) {
            this.setSnakeActive(false);
            this.setLionActive(true);
        }
        this.updateDescription();
    }

    public void activateAll() {
        this.flash();
        this.amount = 0;
        if (this.lionActive) {
            this.setLionActive(false);
        }
        if (this.goatActive) {
            this.setGoatActive(false);
        }
        if (this.snakeActive) {
            this.setSnakeActive(false);
        }
        this.setLionActive(true);
        this.setGoatActive(true);
        this.setSnakeActive(true);
        this.updateDescription();
    }

    public boolean areAllActive() {
        return this.lionActive && this.goatActive && this.snakeActive;
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (this.lionActive && damageAmount > 0 && info.type != DamageInfo.DamageType.THORNS) {
            this.chimera().addStatus();
        }
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (!this.areAllActive()) {
            this.amount++;
            if (this.amount >= SWITCH_THRESHOLD) {
                this.switchToNextHead();
            }
            this.updateDescription();
        }
    }

    private void setLionActive(boolean isActive) {
        this.lionActive = isActive;
    }

    private void setGoatActive(boolean isActive) {
        if (!this.goatActive && isActive) {
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new MalleablePower(this.owner, MALLEABLE_AMOUNT), MALLEABLE_AMOUNT));
        }
        if (this.goatActive && !isActive) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, MalleablePower.POWER_ID));
        }

        this.goatActive = isActive;
    }

    private void setSnakeActive(boolean isActive) {
        if (!this.snakeActive && isActive) {
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, this.owner, new SerpentsGazePower(this.owner, SERPENTS_GAZE_AMOUNT), SERPENTS_GAZE_AMOUNT));
        }
        if (this.snakeActive && !isActive) {
            this.addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, this.owner, SerpentsGazePower.POWER_ID));
        }

        this.snakeActive = isActive;
    }

    private Chimera chimera() {
        return (Chimera)this.owner;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

