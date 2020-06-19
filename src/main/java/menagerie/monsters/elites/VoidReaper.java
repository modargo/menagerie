package menagerie.monsters.elites;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import menagerie.Menagerie;
import menagerie.cards.spells.Dismember;
import menagerie.powers.DrawReductionSingleTurnPower;

public class VoidReaper extends CustomMonster
{
    public static final String ID = "Menagerie:VoidReaper";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte CUT_THE_FUTURE_ATTACK = 1;
    private static final byte REAP_ATTACK = 2;
    private static final byte SCYTHE_DANCE_ATTACK = 3;
    private static final int CUT_THE_FUTURE_DAMAGE = 13;
    private static final int A3_CUT_THE_FUTURE_DAMAGE = 15;
    private static final int CUT_THE_FUTURE_DRAW_REDUCTION = 2;
    private static final int A18_CUT_THE_FUTURE_DRAW_REDUCTION = 2;
    private static final int REAP_DAMAGE = 11;
    private static final int A3_REAP_DAMAGE = 13;
    private static final int SCYTHE_DANCE_DAMAGE = 4;
    private static final int A3_SCYTHE_DANCE_DAMAGE = 5;
    private static final int SCYTHE_DANCE_HITS = 3;
    private static final int SCYTHE_DANCE_BLOCK = 1;
    private static final int A8_SCYTHE_DANCE_BLOCK = 2;
    private static final int HP_MIN = 102;
    private static final int HP_MAX = 108;
    private static final int A8_HP_MIN = 106;
    private static final int A8_HP_MAX = 112;
    private int cutTheFutureDamage;
    private int cutTheFutureDrawReduction;
    private int reapDamage;
    private int scytheDanceDamage;
    private int scytheDanceHits;
    private int scytheDanceBlock;

    public VoidReaper() {
        this(0.0f, 0.0f);
    }

    public VoidReaper(final float x, final float y) {
        super(VoidReaper.NAME, ID, HP_MAX, -5.0F, 0, 255.0f, 355.0f, IMG, x, y);
        this.type = EnemyType.ELITE;
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(A8_HP_MIN, A8_HP_MAX);
            this.scytheDanceBlock = A8_SCYTHE_DANCE_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.scytheDanceBlock = SCYTHE_DANCE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.cutTheFutureDamage = A3_CUT_THE_FUTURE_DAMAGE;
            this.reapDamage = A3_REAP_DAMAGE;
            this.scytheDanceDamage = A3_SCYTHE_DANCE_DAMAGE;
        } else {
            this.cutTheFutureDamage = CUT_THE_FUTURE_DAMAGE;
            this.reapDamage = REAP_DAMAGE;
            this.scytheDanceDamage = SCYTHE_DANCE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.cutTheFutureDamage));
        this.damage.add(new DamageInfo(this, this.reapDamage));
        this.damage.add(new DamageInfo(this, this.scytheDanceDamage));

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.cutTheFutureDrawReduction = A18_CUT_THE_FUTURE_DRAW_REDUCTION;
            this.scytheDanceHits = SCYTHE_DANCE_HITS;
        }
        else {
            this.cutTheFutureDrawReduction = CUT_THE_FUTURE_DRAW_REDUCTION;
            this.scytheDanceHits = SCYTHE_DANCE_HITS;
        }
    }

    @Override
    public void usePreBattleAction() {
        this.addToBot(new MakeTempCardInHandAction(new Dismember(), false));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case CUT_THE_FUTURE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DrawReductionSingleTurnPower(AbstractDungeon.player, this.cutTheFutureDrawReduction)));
                break;
            case REAP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
            case SCYTHE_DANCE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.scytheDanceBlock));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if ((this.firstMove && AbstractDungeon.ascensionLevel >= 18) || this.lastMove(SCYTHE_DANCE_ATTACK)) {
            this.setMove(MOVES[0], CUT_THE_FUTURE_ATTACK, Intent.ATTACK_DEBUFF, this.cutTheFutureDamage);
        } else if (this.lastMove(CUT_THE_FUTURE_ATTACK)) {
            this.setMove(MOVES[1], REAP_ATTACK, Intent.ATTACK, this.reapDamage);
        } else {
            this.setMove(MOVES[2], SCYTHE_DANCE_ATTACK, Intent.ATTACK_DEFEND, this.scytheDanceDamage, this.scytheDanceHits, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = VoidReaper.monsterStrings.NAME;
        MOVES = VoidReaper.monsterStrings.MOVES;
    }
}