package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import menagerie.Menagerie;
import menagerie.powers.LeafSporesPower;

public class GraftedWorm extends CustomMonster
{
    public static final String ID = "Menagerie:GraftedWorm";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte LEAF_BLADE_ATTACK = 1;
    private static final int LEAF_BLADE_DAMAGE = 3;
    private static final int A2_LEAF_BLADE_DAMAGE = 4;
    private static final int LEAF_SPORES_AMOUNT = 1;
    private static final int A17_LEAF_SPORES_AMOUNT = 2;
    private static final int HP_MIN = 9;
    private static final int HP_MAX = 11;
    private static final int A7_HP_MIN = 10;
    private static final int A7_HP_MAX = 12;
    private int leafBladeDamage;
    private int leafSporesAmount;

    public GraftedWorm() {
        this(0.0f, 0.0f);
    }

    public GraftedWorm(final float x, final float y) {
        super(GraftedWorm.NAME, ID, HP_MAX, -5.0F, 0, 155.0f, 150.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.leafBladeDamage = A2_LEAF_BLADE_DAMAGE;
        } else {
            this.leafBladeDamage = LEAF_BLADE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.leafBladeDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.leafSporesAmount = A17_LEAF_SPORES_AMOUNT;
        } else {
            this.leafSporesAmount = LEAF_SPORES_AMOUNT;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new LeafSporesPower(this, this.leafSporesAmount)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case LEAF_BLADE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.setMove(MOVES[0], LEAF_BLADE_ATTACK, Intent.ATTACK, this.leafBladeDamage);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GraftedWorm.monsterStrings.NAME;
        MOVES = GraftedWorm.monsterStrings.MOVES;
    }
}