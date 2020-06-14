package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import menagerie.Menagerie;

public class ProwlingAmalgam extends CustomMonster
{
    public static final String ID = "Menagerie:ProwlingAmalgam";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte BITE_AND_STOMP_ATTACK = 1;
    private static final byte RABID_FRENZY_ATTACK = 2;
    private static final int BITE_AND_STOMP_DAMAGE = 7;
    private static final int A2_BITE_AND_STOMP_DAMAGE = 8;
    private static final int RABID_FRENZY_DAMAGE = 3;
    private static final int A2_RABID_FRENZY_DAMAGE = 4;
    private static final int RABID_FRENZY_HITS = 3;
    private static final int A17_RABID_FRENZY_HITS = 4;
    private static final int HP_MIN = 51;
    private static final int HP_MAX = 54;
    private static final int A7_HP_MIN = 53;
    private static final int A7_HP_MAX = 56;
    private int biteAndStompDamage;
    private int rabidFrenzyDamage;
    private int rabidFrenzyHits;

    public ProwlingAmalgam() {
        this(0.0f, 0.0f);
    }

    public ProwlingAmalgam(final float x, final float y) {
        super(ProwlingAmalgam.NAME, ID, HP_MAX, -5.0F, 0, 355.0f, 305.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.biteAndStompDamage = A2_BITE_AND_STOMP_DAMAGE;
            this.rabidFrenzyDamage = A2_RABID_FRENZY_DAMAGE;
        } else {
            this.biteAndStompDamage = BITE_AND_STOMP_DAMAGE;
            this.rabidFrenzyDamage = RABID_FRENZY_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.biteAndStompDamage));
        this.damage.add(new DamageInfo(this, this.rabidFrenzyDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.rabidFrenzyHits = A17_RABID_FRENZY_HITS;
        } else {
            this.rabidFrenzyHits = RABID_FRENZY_HITS;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case BITE_AND_STOMP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            case RABID_FRENZY_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < this.rabidFrenzyHits; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                }
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
        if (hasAllies) {
            this.setMove(MOVES[0], BITE_AND_STOMP_ATTACK, Intent.ATTACK, this.biteAndStompDamage);
        } else {
            this.setMove(MOVES[1], RABID_FRENZY_ATTACK, Intent.ATTACK, this.rabidFrenzyDamage, this.rabidFrenzyHits, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = ProwlingAmalgam.monsterStrings.NAME;
        MOVES = ProwlingAmalgam.monsterStrings.MOVES;
    }
}