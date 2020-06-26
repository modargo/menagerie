package menagerie.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.Menagerie;
import menagerie.monsters.elites.FrozenSoldier;
import menagerie.monsters.elites.MaskedSummoner;

import java.text.MessageFormat;

public class ThawingPower extends AbstractPower {
    public static final String POWER_ID = "Menagerie:Thawing";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int HEALTH_DAMAGE_MULTIPLIER = 3;
    private static final int DAMAGE_WHEN_KILLED = 10;
    private boolean justApplied = true;

    public ThawingPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.isTurnBased = true;
        this.updateDescription();
        this.priority = 50;
        Menagerie.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount, HEALTH_DAMAGE_MULTIPLIER, DAMAGE_WHEN_KILLED);
    }

    @Override
    public void atEndOfRound() {
        if (this.justApplied) {
            this.justApplied = false;
        } else {
            if (this.amount == 0) {
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            } else {
                this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
            }
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage + (float)this.owner.currentHealth * HEALTH_DAMAGE_MULTIPLIER : damage;
    }

    @Override
    public void onDeath() {
        if (!((FrozenSoldier)this.owner).violentThawUsed) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != this.owner && !m.isDying && m.id.equals(MaskedSummoner.ID)) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(m, DAMAGE_WHEN_KILLED, DamageInfo.DamageType.HP_LOSS)));
                }
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

