package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import menagerie.Menagerie;
import menagerie.powers.MeltingPower;

public class MeltingSalamander extends CustomMonster
{
    public static final String ID = "Menagerie:MeltingSalamander";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte FIERY_SPIT_ATTACK = 1;
    private static final byte OVERHEAT_MOVE = 2;
    private static final byte MELTDOWN_MOVE = 3;
    private static final int FIERY_SPIT_DAMAGE = 9;
    private static final int A2_FIERY_SPIT_DAMAGE = 10;
    private static final int MELTDOWN_DAMAGE = 23;
    private static final int A2_MELTDOWN_DAMAGE = 26;
    private static final int MELTDOWN_SELF_DAMAGE = 5;
    private static final int MELTDOWN_FRAIL_AND_WEAK = 3;
    private static final int STARTING_METALLICIZE = 6;
    private static final int A17_STARTING_METALLICIZE = 9;
    private static final int MELTING_AMOUNT = 2;
    private static final int A17_MELTING_AMOUNT = 3;
    private static final int HP_MIN = 56;
    private static final int HP_MAX = 61;
    private static final int A7_HP_MIN = 59;
    private static final int A7_HP_MAX = 64;
    private int fierySpitDamage;
    private int meltdownDamage;
    private int meltingAmount;
    private int startingMetallicize;

    public MeltingSalamander() {
        this(0.0f, 0.0f);
    }

    public MeltingSalamander(final float x, final float y) {
        super(MeltingSalamander.NAME, ID, HP_MAX, -5.0F, 0, 405.0f, 220.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.fierySpitDamage = A2_FIERY_SPIT_DAMAGE;
            this.meltdownDamage = A2_MELTDOWN_DAMAGE;
        } else {
            this.fierySpitDamage = FIERY_SPIT_DAMAGE;
            this.meltdownDamage = MELTDOWN_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.fierySpitDamage));
        this.damage.add(new DamageInfo(this, this.meltdownDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.startingMetallicize = A17_STARTING_METALLICIZE;
            this.meltingAmount = A17_MELTING_AMOUNT;
        }
        else {
            this.startingMetallicize = STARTING_METALLICIZE;
            this.meltingAmount = MELTING_AMOUNT;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.startingMetallicize));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, this.startingMetallicize), this.startingMetallicize));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MeltingPower(this, this.meltingAmount), this.meltingAmount));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case FIERY_SPIT_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                break;
            case OVERHEAT_MOVE:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
                break;
            case MELTDOWN_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ScreenOnFireEffect(), 1.0F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, MELTDOWN_FRAIL_AND_WEAK, true), MELTDOWN_FRAIL_AND_WEAK));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, MELTDOWN_FRAIL_AND_WEAK, true), MELTDOWN_FRAIL_AND_WEAK));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(this, new DamageInfo(this, MELTDOWN_SELF_DAMAGE, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, this.startingMetallicize), this.startingMetallicize));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(OVERHEAT_MOVE)) {
            this.setMove(MOVES[2], MELTDOWN_MOVE, Intent.ATTACK_DEBUFF, this.meltdownDamage);
        }
        else if (this.lastMove(FIERY_SPIT_ATTACK) && this.lastMoveBefore(FIERY_SPIT_ATTACK)) {
            this.setMove(MOVES[1], OVERHEAT_MOVE, Intent.UNKNOWN);
        }
        else {
            this.setMove(MOVES[0], FIERY_SPIT_ATTACK, Intent.ATTACK, this.fierySpitDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = MeltingSalamander.monsterStrings.NAME;
        MOVES = MeltingSalamander.monsterStrings.MOVES;
    }
}