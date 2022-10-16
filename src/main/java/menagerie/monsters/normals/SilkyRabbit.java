package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import menagerie.Menagerie;

public class SilkyRabbit extends CustomMonster
{
    public static final String ID = "Menagerie:SilkyRabbit";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte BITE_ATTACK = 1;
    private static final byte EAR_SWAT_ATTACK = 2;
    private static final int BITE_DAMAGE = 6;
    private static final int A2_BITE_DAMAGE = 7;
    private static final int EAR_SWAT_DAMAGE = 3;
    private static final int A2_EAR_SWAT_DAMAGE = 4;
    private static final int EAR_SWAT_WEAK = 1;
    private static final int A17_EAR_SWAT_WEAK = 2;
    private static final int HP_MIN = 21;
    private static final int HP_MAX = 26;
    private static final int A7_HP_MIN = 23;
    private static final int A7_HP_MAX = 28;
    private int biteDamage;
    private int earSwatDamage;
    private int earSwatWeak;

    public SilkyRabbit() {
        this(0.0f, 0.0f);
    }

    public SilkyRabbit(final float x, final float y) {
        super(SilkyRabbit.NAME, ID, HP_MAX, -5.0F, 0, 155.0f, 150.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.biteDamage = A2_BITE_DAMAGE;
            this.earSwatDamage = A2_EAR_SWAT_DAMAGE;
        } else {
            this.biteDamage = BITE_DAMAGE;
            this.earSwatDamage = EAR_SWAT_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.biteDamage));
        this.damage.add(new DamageInfo(this, this.earSwatDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.earSwatWeak = A17_EAR_SWAT_WEAK;
        }
        else {
            this.earSwatWeak = EAR_SWAT_WEAK;
        }
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
            case EAR_SWAT_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.earSwatWeak, true), this.earSwatWeak));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if ((num < 65 && !(this.lastMove(BITE_ATTACK) && this.lastMoveBefore(BITE_ATTACK)) || this.lastMove(EAR_SWAT_ATTACK))) {
            this.setMove(MOVES[0], BITE_ATTACK, Intent.ATTACK, this.biteDamage);
        }
        else {
            this.setMove(MOVES[1], EAR_SWAT_ATTACK, Intent.ATTACK_DEBUFF, this.earSwatDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = SilkyRabbit.monsterStrings.NAME;
        MOVES = SilkyRabbit.monsterStrings.MOVES;
    }
}