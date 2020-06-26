package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import menagerie.Menagerie;
import menagerie.cards.PurgingElixir;
import menagerie.powers.EntropyAuraPower;

public class EntropyWarlock extends CustomMonster
{
    public static final String ID = "Menagerie:EntropyWarlock";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte DARK_INCANTATION_ATTACK = 1;
    private static final byte TENTACLE_GRASP_ATTACK = 2;
    private static final int DARK_INCANTATION_DAMAGE = 8;
    private static final int A2_DARK_INCANTATION_DAMAGE = 9;
    private static final int TENTACLE_GRASP_DAMAGE = 3;
    private static final int A2_TENTACLE_GRASP_DAMAGE = 4;
    private static final int TENTACLE_GRASP_CONSTRICT = 1;
    private static final int A17_TENTACLE_GRASP_CONSTRICT = 2;
    private static final int HP_MIN = 51;
    private static final int HP_MAX = 55;
    private static final int A7_HP_MIN = 54;
    private static final int A7_HP_MAX = 58;
    private int darkIncantationDamage;
    private int tentacleGraspDamage;
    private int tentacleGraspConstrict;

    public EntropyWarlock() {
        this(0.0f, 0.0f);
    }

    public EntropyWarlock(final float x, final float y) {
        super(EntropyWarlock.NAME, ID, HP_MAX, -5.0F, 0, 150.0f, 305.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.darkIncantationDamage = A2_DARK_INCANTATION_DAMAGE;
            this.tentacleGraspDamage = A2_TENTACLE_GRASP_DAMAGE;
        } else {
            this.darkIncantationDamage = DARK_INCANTATION_DAMAGE;
            this.tentacleGraspDamage = TENTACLE_GRASP_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.darkIncantationDamage));
        this.damage.add(new DamageInfo(this, this.tentacleGraspDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.tentacleGraspConstrict = A17_TENTACLE_GRASP_CONSTRICT;
        } else {
            this.tentacleGraspConstrict = TENTACLE_GRASP_CONSTRICT;
        }
    }

    @Override
    public void usePreBattleAction() {
        this.addToBot(new MakeTempCardInHandAction(new PurgingElixir(), false));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new EntropyAuraPower(AbstractDungeon.player)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case DARK_INCANTATION_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                this.addToBot(new VFXAction(this, new BorderLongFlashEffect(Color.MAGENTA), 0.0F, true));
                this.addToBot(new VFXAction(this, new BorderLongFlashEffect(Color.BLACK), 0.0F, true));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                break;
            case TENTACLE_GRASP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new ConstrictedPower(AbstractDungeon.player, this, this.tentacleGraspConstrict), this.tentacleGraspConstrict));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if((this.firstMove && AbstractDungeon.ascensionLevel >= 17) || (!this.firstMove && num < 40 && !this.lastMove(TENTACLE_GRASP_ATTACK)) || (this.lastMove(DARK_INCANTATION_ATTACK) && this.lastMoveBefore(DARK_INCANTATION_ATTACK))) {
            this.setMove(MOVES[1], TENTACLE_GRASP_ATTACK, Intent.ATTACK_DEBUFF, this.tentacleGraspDamage);
        }
        else {
            this.setMove(MOVES[0], DARK_INCANTATION_ATTACK, Intent.ATTACK, this.darkIncantationDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = EntropyWarlock.monsterStrings.NAME;
        MOVES = EntropyWarlock.monsterStrings.MOVES;
    }
}