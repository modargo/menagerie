package menagerie.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;
import menagerie.powers.SquabblingHeadsPower;

public class Chimera extends CustomMonster
{
    public static final String ID = "Menagerie:Chimera";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte TRIPLE_BITE_ATTACK = 1;
    private static final byte SQUABBLE_MOVE = 2;
    private static final byte CLAW_AND_HORN_ATTACK = 3;
    private static final byte VENOM_SPIT_ATTACK = 4;
    private static final byte THREE_AS_ONE_BUFF = 5;
    private static final int TRIPLE_BITE_DAMAGE = 4;
    private static final int A4_TRIPLE_BITE_DAMAGE = 5;
    private static final int TRIPLE_BITE_HITS = 3;
    private static final int SQUABBLE_BLOCK = 10;
    private static final int A9_SQUABBLE_BLOCK = 15;
    private static final int CLAW_AND_HORN_DAMAGE = 15;
    private static final int A4_CLAW_AND_HORN_DAMAGE = 17;
    private static final int CLAW_AND_HORN_BLOCK = 5;
    private static final int VENOM_SPIT_DAMAGE = 12;
    private static final int A4_VENOM_SPIT_DAMAGE = 14;
    private static final int THREE_AS_ONE_STRENGTH = 2;
    private static final int HP = 210;
    private static final int A9_HP = 220;
    private static final AbstractCard[] statuses = new AbstractCard[]{ new Slimed(), new Wound(), new Burn() };
    private int tripleBiteDamage;
    private int squabbleBlock;
    private int clawAndHornDamage;
    private int venomSpitDamage;

    private SquabblingHeadsPower power;
    private int statusCount = 0;

    public Chimera() {
        this(0.0f, 0.0f);
    }

    public Chimera(final float x, final float y) {
        super(Chimera.NAME, ID, HP, -5.0F, 0, 555.0f, 420.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
            this.squabbleBlock = A9_SQUABBLE_BLOCK;
        } else {
            this.setHp(HP);
            this.squabbleBlock = SQUABBLE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.tripleBiteDamage = A4_TRIPLE_BITE_DAMAGE;
            this.clawAndHornDamage = A4_CLAW_AND_HORN_DAMAGE;
            this.venomSpitDamage = A4_VENOM_SPIT_DAMAGE;
        } else {
            this.tripleBiteDamage = TRIPLE_BITE_DAMAGE;
            this.clawAndHornDamage = CLAW_AND_HORN_DAMAGE;
            this.venomSpitDamage = VENOM_SPIT_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tripleBiteDamage));
        this.damage.add(new DamageInfo(this, this.clawAndHornDamage));
        this.damage.add(new DamageInfo(this, this.venomSpitDamage));
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");

        this.power = new SquabblingHeadsPower(this);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, this.power));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case TRIPLE_BITE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            case SQUABBLE_MOVE:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.squabbleBlock));
                if (AbstractDungeon.ascensionLevel >= 19) {
                    this.addStatus();
                }
                this.switchToNextHead();
                break;
            case CLAW_AND_HORN_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SHIELD));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, CLAW_AND_HORN_BLOCK));
                break;
            case VENOM_SPIT_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.POISON));
                this.addStatus();
                if (AbstractDungeon.ascensionLevel >= 19) {
                    this.addStatus();
                }
                break;
            case THREE_AS_ONE_BUFF:
                this.activateTogetherMode();
                if (AbstractDungeon.ascensionLevel >= 19) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, THREE_AS_ONE_STRENGTH), THREE_AS_ONE_STRENGTH));
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        boolean togetherMode = this.togetherMode();
        if (this.currentHealth <= this.maxHealth / 2 && !togetherMode) {
            this.setMove(MOVES[4], THREE_AS_ONE_BUFF, Intent.BUFF);
        }
        else if (this.firstMove || this.lastMove(THREE_AS_ONE_BUFF) || ((this.lastMove(CLAW_AND_HORN_ATTACK) || this.lastMove(VENOM_SPIT_ATTACK)) && (this.lastMoveBefore(CLAW_AND_HORN_ATTACK) || this.lastMoveBefore(VENOM_SPIT_ATTACK)))) {
            this.setMove(MOVES[0], TRIPLE_BITE_ATTACK, Intent.ATTACK, this.tripleBiteDamage, TRIPLE_BITE_HITS, true);
        } else if (this.lastMove(TRIPLE_BITE_ATTACK) && !togetherMode) {
            this.setMove(MOVES[1], SQUABBLE_MOVE, Intent.DEFEND_BUFF);
        } else if (((this.lastMove(TRIPLE_BITE_ATTACK) || this.lastMove(SQUABBLE_MOVE)) && num > 50)
            || (this.lastMove(VENOM_SPIT_ATTACK) && !this.lastMoveBefore(CLAW_AND_HORN_ATTACK))) {
            this.setMove(MOVES[2], CLAW_AND_HORN_ATTACK, Intent.ATTACK_DEFEND, this.clawAndHornDamage);
        } else {
            this.setMove(MOVES[3], VENOM_SPIT_ATTACK, Intent.ATTACK_DEBUFF, this.venomSpitDamage);
        }
    }

    @Override
    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            this.onBossVictoryLogic();
        }
    }

    private void switchToNextHead() {
        this.power.switchToNextHead();
    }

    public void addStatus() {
        AbstractCard status = statuses[this.statusCount % statuses.length].makeCopy();
        this.addToBot(new MakeTempCardInDiscardAction(status, 1));
        this.statusCount++;
    }

    private void activateTogetherMode() {
        this.power.activateAll();
    }

    private boolean togetherMode() {
        return this.power != null && this.power.areAllActive();
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = Chimera.monsterStrings.NAME;
        MOVES = Chimera.monsterStrings.MOVES;
    }
}