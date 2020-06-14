package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import menagerie.Menagerie;

public class BeastMage extends CustomMonster
{
    public static final String ID = "Menagerie:BeastMage";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte ENERVATE_DEBUFF = 1;
    private static final byte HEX_DEBUFF = 2;
    private static final byte ARCANE_BULWARK_MOVE = 3;
    private static final byte BESTIAL_RAGE_ATTACK = 4;
    private static final int ENERVATE_WEAK = 1;
    private static final int A17_ENERVATE_WEAK = 2;
    private static final int HEX_VULNERABLE = 1;
    private static final int A17_HEX_VULNERABLE = 2;
    private static final int ARCANE_BULWARK_BLOCK = 8;
    private static final int A7_ARCANE_BULWARK_BLOCK = 10;
    private static final int BESTIAL_RAGE_DAMAGE = 11;
    private static final int A2_BESTIAL_RAGE_DAMAGE = 13;
    private static final int BESTIAL_RAGE_BLOCK = 8;
    private static final int A7_BESTIAL_RAGE_BLOCK = 10;
    private static final int HP_MIN = 45;
    private static final int HP_MAX = 50;
    private static final int A7_HP_MIN = 48;
    private static final int A7_HP_MAX = 53;
    private int enervateWeak;
    private int hexVulnerable;
    private int arcaneBulwarkBlock;
    private int bestialRageDamage;
    private int bestialRageBlock;

    public BeastMage() {
        this(0.0f, 0.0f);
    }

    public BeastMage(final float x, final float y) {
        super(BeastMage.NAME, ID, HP_MAX, -5.0F, 0, 150.0f, 305.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.arcaneBulwarkBlock = A7_ARCANE_BULWARK_BLOCK;
            this.bestialRageBlock = A7_BESTIAL_RAGE_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.arcaneBulwarkBlock = ARCANE_BULWARK_BLOCK;
            this.bestialRageBlock = BESTIAL_RAGE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.bestialRageDamage = A2_BESTIAL_RAGE_DAMAGE;
        } else {
            this.bestialRageDamage = BESTIAL_RAGE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.bestialRageDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.enervateWeak = A17_ENERVATE_WEAK;
            this.hexVulnerable = A17_HEX_VULNERABLE;
        } else {
            this.enervateWeak = ENERVATE_WEAK;
            this.hexVulnerable = HEX_VULNERABLE;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case ENERVATE_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.enervateWeak, true), this.enervateWeak));
                break;
            case HEX_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.hexVulnerable, true), this.hexVulnerable));
                break;
            case ARCANE_BULWARK_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this.arcaneBulwarkBlock));
                    }
                }
                break;
            case BESTIAL_RAGE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.bestialRageBlock));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        boolean hasAllies = false;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this && !m.isDying) {
                hasAllies = true;
                break;
            }
        }
        if (!hasAllies && !this.lastMove(BESTIAL_RAGE_ATTACK)) {
            this.setMove(MOVES[3], BESTIAL_RAGE_ATTACK, Intent.ATTACK_DEFEND, this.bestialRageDamage);
        }
        else if (!hasAllies || this.lastMove(ENERVATE_DEBUFF)) {
            this.setMove(MOVES[1], HEX_DEBUFF, Intent.STRONG_DEBUFF);
        }
        else if (this.lastMove(HEX_DEBUFF)) {
            this.setMove(MOVES[2], ARCANE_BULWARK_MOVE, Intent.DEFEND);
        }
        else {
            this.setMove(MOVES[0], ENERVATE_DEBUFF, Intent.DEBUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = BeastMage.monsterStrings.NAME;
        MOVES = BeastMage.monsterStrings.MOVES;
    }
}