package menagerie.monsters.elites;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import menagerie.Menagerie;
import menagerie.actions.SummonFrozenSoldierAction;

public class MaskedSummoner extends CustomMonster
{
    public static final String ID = "Menagerie:MaskedSummoner";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte GENTLE_SNOW_BUFF = 1;
    private static final byte FROST_SUMMON_MOVE = 2;
    private static final byte SILENT_SPEAR_ATTACK = 3;
    private static final int GENTLE_SNOW_HEAL = 0;
    private static final int A18_GENTLE_SNOW_HEAL = 1;
    private static final int GENTLE_SNOW_STRENGTH = 2;
    private static final int A18_GENTLE_SNOW_STRENGTH = 2;
    private static final int GENTLE_SNOW_BLOCK = 4;
    private static final int A8_GENTLE_SNOW_BLOCK = 6;
    private static final int FROST_SUMMON_COUNT = 2;
    private static final int SILENT_SPEAR_DAMAGE = 8;
    private static final int A3_SILENT_SPEAR_DAMAGE = 10;
    private static final int STARTING_SUMMONS = 2;
    private static final int A18_STARTING_SUMMONS = 2;
    private static final int HP_MIN = 112;
    private static final int HP_MAX = 118;
    private static final int A8_HP_MIN = 116;
    private static final int A8_HP_MAX = 122;
    private int gentleSnowHeal;
    private int gentleSnowStrength;
    private int gentleSnowBlock;
    private int silentSpearDamage;
    private int startingSummons;
    private AbstractMonster[] activeMinions = new AbstractMonster[5];

    public MaskedSummoner() {
        this(0.0f, 0.0f);
    }

    public MaskedSummoner(final float x, final float y) {
        super(MaskedSummoner.NAME, ID, HP_MAX, -5.0F, 0, 180.0f, 380.0f, IMG, x, y);
        this.type = EnemyType.ELITE;
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(A8_HP_MIN, A8_HP_MAX);
            this.gentleSnowBlock = A8_GENTLE_SNOW_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.gentleSnowBlock = GENTLE_SNOW_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.silentSpearDamage = A3_SILENT_SPEAR_DAMAGE;
        } else {
            this.silentSpearDamage = SILENT_SPEAR_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.silentSpearDamage));

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.gentleSnowHeal = A18_GENTLE_SNOW_HEAL;
            this.gentleSnowStrength = A18_GENTLE_SNOW_STRENGTH;
            this.startingSummons = A18_STARTING_SUMMONS;
        }
        else {
            this.gentleSnowHeal = GENTLE_SNOW_HEAL;
            this.gentleSnowStrength = GENTLE_SNOW_STRENGTH;
            this.startingSummons = STARTING_SUMMONS;
        }
    }

    @Override
    public void usePreBattleAction() {
        this.summonFrozenSoldiers(this.startingSummons, true);
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case GENTLE_SNOW_BUFF:
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m != this && !m.isDying) {
                        if (this.gentleSnowHeal > 0) {
                            AbstractDungeon.actionManager.addToBottom(new HealAction(m, this, this.gentleSnowHeal));
                        }
                        if (this.gentleSnowStrength > 0) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.gentleSnowStrength), this.gentleSnowStrength));
                        }
                    }
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.gentleSnowBlock));
                break;
            case FROST_SUMMON_MOVE:
                this.summonFrozenSoldiers(FROST_SUMMON_COUNT, false);
                break;
            case SILENT_SPEAR_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int minionCount = 0;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this && !m.isDying && m.id.equals(FrozenSoldier.ID)) {
                minionCount++;
                break;
            }
        }
        byte move = this.getMove(minionCount, num);
        if (move == GENTLE_SNOW_BUFF) {
            this.setMove(MOVES[0], move, Intent.DEFEND_BUFF);
        }
        else if (move == FROST_SUMMON_MOVE) {
            this.setMove(MOVES[1], move, Intent.UNKNOWN);
        }
        else {
            this.setMove(MOVES[2], move, Intent.ATTACK, this.silentSpearDamage);
        }
    }

    private byte getMove(int minionCount, int num) {
        /* This follows the table:
         * Buff (Gentle Snow): 0/0/20/40/60/60
         * Summon (Frost Summoning): 100/75/50/25/0/0
         * Attack (Silent Spear): 0/25/30/35/40/40
         */
        switch (minionCount) {
            case 0:
                return FROST_SUMMON_MOVE;
            case 1:
                if (this.lastMove(FROST_SUMMON_MOVE)) {
                    return SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(SILENT_SPEAR_ATTACK)) {
                    return FROST_SUMMON_MOVE;
                }
                return num < 75 ? FROST_SUMMON_MOVE : SILENT_SPEAR_ATTACK;
            case 2:
                if (this.lastMove(GENTLE_SNOW_BUFF)) {
                    return num < 63 ? FROST_SUMMON_MOVE : SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(FROST_SUMMON_MOVE)) {
                    return num < 40 ? GENTLE_SNOW_BUFF : SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(SILENT_SPEAR_ATTACK)) {
                    return num < 29 ? GENTLE_SNOW_BUFF : FROST_SUMMON_MOVE;
                }
                return num < 20 ? GENTLE_SNOW_BUFF : num < 70 ? FROST_SUMMON_MOVE : SILENT_SPEAR_ATTACK;
            case 3:
                if (this.lastMove(GENTLE_SNOW_BUFF)) {
                    return num < 42 ? FROST_SUMMON_MOVE : SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(FROST_SUMMON_MOVE)) {
                    return num < 53 ? GENTLE_SNOW_BUFF : SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(SILENT_SPEAR_ATTACK)) {
                    return num < 62 ? GENTLE_SNOW_BUFF : FROST_SUMMON_MOVE;
                }
                return num < 40 ? GENTLE_SNOW_BUFF : num < 65 ? FROST_SUMMON_MOVE : SILENT_SPEAR_ATTACK;
            default:
                if (this.lastMove(GENTLE_SNOW_BUFF)) {
                    return SILENT_SPEAR_ATTACK;
                }
                if (this.lastMove(SILENT_SPEAR_ATTACK)) {
                    return GENTLE_SNOW_BUFF;
                }
                return num < 60 ? GENTLE_SNOW_BUFF : SILENT_SPEAR_ATTACK;
        }
    }

    private void summonFrozenSoldiers(int numberToSummon, boolean firstTurn) {
        for (int i = 0; i < numberToSummon; i++) {
            int slot = this.getFirstFreeMinionSlot();
            float xPosition = this.slotToXPosition(slot);
            AbstractDungeon.actionManager.addToBottom(new SummonFrozenSoldierAction(xPosition, 0.0F, firstTurn, this.activeMinions, slot));
        }
    }

    private int getFirstFreeMinionSlot(){
        for(int i = 0; i < this.activeMinions.length; ++i) {
            if (this.activeMinions[i] == null || this.activeMinions[i].isDying) {
                return i;
            }
        }

        return -1;
    }

    private float slotToXPosition(int slot) {
        switch (slot) {
            case 0: return -0.0F;
            case 1: return -150.0F;
            case 2: return -300.0F;
            case 3: return -450.0F;
            default: return -600.0F;
        }
    }

    @Override
    public void die() {
        super.die();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m.id.equals(FrozenSoldier.ID) && !m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ExplosionSmallEffect(m.hb.cX, m.hb.cY), 0.1F));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(m));
            }
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = MaskedSummoner.monsterStrings.NAME;
        MOVES = MaskedSummoner.monsterStrings.MOVES;
    }
}