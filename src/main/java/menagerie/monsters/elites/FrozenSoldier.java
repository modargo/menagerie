package menagerie.monsters.elites;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import menagerie.Menagerie;
import menagerie.powers.ThawingPower;

public class FrozenSoldier extends CustomMonster
{
    public static final String ID = "Menagerie:FrozenSoldier";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte ICE_BARRIER_MOVE = 1;
    private static final byte VIOLENT_THAW_ATTACK = 2;
    private static final int EXPLOSIVE_THAW_DAMAGE = 1;
    private static final int A3_EXPLOSIVE_THAW_DAMAGE = 2;
    private static final int ICE_BARRIER_BLOCK = 2;
    private static final int A8_ICE_BARRIER_BLOCK = 3;
    private static final int THAWING_AMOUNT = 3;
    private static final int A18_THAWING_AMOUNT = 3;
    private static final int HP = 3;
    private static final int A18_HP = 3;
    private int iceBarrierBlock;
    private int explosiveThawDamage;
    private int thawingAmount;
    private boolean startOfCombat;

    public FrozenSoldier() {
        this(0.0f, 0.0f, false);
    }

    public FrozenSoldier(final float x, final float y, boolean startOfCombat) {
        super(FrozenSoldier.NAME, ID, HP, -5.0F, 0, 130.0f, 150.0f, IMG, x, y);
        this.type = EnemyType.ELITE;
        if (AbstractDungeon.ascensionLevel >= 18) {
            this.setHp(A18_HP);
            this.thawingAmount = A18_THAWING_AMOUNT;
        } else {
            this.setHp(HP);
            this.thawingAmount = THAWING_AMOUNT;
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.explosiveThawDamage = A3_EXPLOSIVE_THAW_DAMAGE;
        } else {
            this.explosiveThawDamage = EXPLOSIVE_THAW_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.explosiveThawDamage));

        if (AbstractDungeon.ascensionLevel >= 8) {
            this.iceBarrierBlock = A8_ICE_BARRIER_BLOCK;
        } else {
            this.iceBarrierBlock = ICE_BARRIER_BLOCK;
        }


        this.startOfCombat = startOfCombat;
    }

    @Override
    public void usePreBattleAction() {
        AbstractPower thawing = new ThawingPower(this, this.thawingAmount);
        AbstractPower intangible = new IntangiblePower(this, this.thawingAmount);
        if (this.startOfCombat) {
            // To force justApplied to be false
            thawing.atEndOfRound();
            intangible.atEndOfTurn(false);
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, thawing));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, intangible));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case ICE_BARRIER_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m != this && !m.isDying && m.id.equals(MaskedSummoner.ID)) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.iceBarrierBlock));
                    }
                }
                break;
            case VIOLENT_THAW_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new RemoveAllBlockAction(this, this));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ExplosionSmallEffect(this.hb.cX, this.hb.cY), 0.1F));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.moveHistory.size() > this.thawingAmount - 2) {
            this.setMove(MOVES[1], VIOLENT_THAW_ATTACK, Intent.ATTACK, this.explosiveThawDamage);
        }
        else {
            this.setMove(MOVES[0], ICE_BARRIER_MOVE, Intent.DEFEND);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.output > 0 && this.hasPower(IntangiblePower.POWER_ID)) {
            info.output = 1;
        }

        super.damage(info);

        // To recalculate attack intent damage, since it changes based on HP due to Thawing
        this.applyPowers();
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = FrozenSoldier.monsterStrings.NAME;
        MOVES = FrozenSoldier.monsterStrings.MOVES;
    }
}