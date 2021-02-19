package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;

public class FeatherRabbit extends CustomMonster
{
    public static final String ID = "Menagerie:FeatherRabbit";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte BITE_ATTACK = 1;
    private static final byte EAR_TINGLE_BUFF = 2;
    private static final int BITE_DAMAGE = 4;
    private static final int A2_BITE_DAMAGE = 5;
    private static final int EAR_TINGLE_BLOCK = 3;
    private static final int A7_EAR_TINGLE_BLOCK = 4;
    private static final int EAR_TINGLE_STRENGTH = 1;
    private static final int HP_MIN = 21;
    private static final int HP_MAX = 26;
    private static final int A7_HP_MIN = 23;
    private static final int A7_HP_MAX = 28;
    private int biteDamage;
    private int earTingleBlock;
    private FirstMove firstMoveDecision;

    public FeatherRabbit() {
        this(0.0f, 0.0f);
    }

    public FeatherRabbit(final float x, final float y) { this (x, y, FirstMove.RANDOM); }

    public FeatherRabbit(final float x, final float y, FirstMove firstMoveDecision) {
        super(FeatherRabbit.NAME, ID, HP_MAX, -5.0F, 0, 155.0f, 150.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        this.firstMoveDecision = firstMoveDecision;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.earTingleBlock = A7_EAR_TINGLE_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.earTingleBlock = EAR_TINGLE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.biteDamage = A2_BITE_DAMAGE;
        } else {
            this.biteDamage = BITE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.biteDamage));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case BITE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                break;
            case EAR_TINGLE_BUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || (AbstractDungeon.ascensionLevel >= 17 && !m.isDying)) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this.earTingleBlock));
                    }
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, EAR_TINGLE_STRENGTH), EAR_TINGLE_STRENGTH));
                    }
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if ((num < 65 && !(this.lastMove(BITE_ATTACK) && this.lastMoveBefore(BITE_ATTACK)) && !(this.firstMove && this.firstMoveDecision == FirstMove.BUFF)) || this.lastMove(EAR_TINGLE_BUFF) || (this.firstMove && this.firstMoveDecision == FirstMove.ATTACK)) {
            this.setMove(MOVES[0], BITE_ATTACK, Intent.ATTACK, this.biteDamage);
        }
        else {
            this.setMove(MOVES[1], EAR_TINGLE_BUFF, Intent.DEFEND_BUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = FeatherRabbit.monsterStrings.NAME;
        MOVES = FeatherRabbit.monsterStrings.MOVES;
    }

    public enum FirstMove {
        RANDOM,
        ATTACK,
        BUFF
    }
}