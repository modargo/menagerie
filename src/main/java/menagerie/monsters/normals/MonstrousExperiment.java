package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.*;
import menagerie.Menagerie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MonstrousExperiment extends CustomMonster
{
    public static final String ID = "Menagerie:MonstrousExperiment";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte SLIMY_APPENDAGE_ATTACK = 1;
    private static final byte THRASH_ATTACK = 2;
    private static final byte DEGENERATE_MOVE = 3;
    private static final int SLIMY_APPENDAGE_DAMAGE = 5;
    private static final int A2_SLIMY_APPENDAGE_DAMAGE = 6;
    private static final int THRASH_DAMAGE = 9;
    private static final int A2_THRASH_DAMAGE = 10;
    private static final int DEGENERATE_BLOCK = 2;
    private static final int A17_DEGENERATE_BLOCK = 8;
    private static final int DEGENERATE_HEALTH_LOSS = 3;
    private static final int A7_DEGENERATE_HEALTH_LOSS = 2;
    private static final int HP_MIN = 42;
    private static final int HP_MAX = 45;
    private static final int A7_HP_MIN = 44;
    private static final int A7_HP_MAX = 47;
    private int slimyAppendageDamage;
    private int thrashDamage;
    private int degenerateBlock;
    private int degenerateHealthLoss;
    private Random random = new Random();

    public MonstrousExperiment() {
        this(0.0f, 0.0f);
    }

    public MonstrousExperiment(final float x, final float y) {
        super(MonstrousExperiment.NAME, ID, HP_MAX, -5.0F, 0, 150.0f, 205.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.degenerateHealthLoss = A7_DEGENERATE_HEALTH_LOSS;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.degenerateHealthLoss = DEGENERATE_HEALTH_LOSS;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.slimyAppendageDamage = A2_SLIMY_APPENDAGE_DAMAGE;
            this.thrashDamage = A2_THRASH_DAMAGE;
        } else {
            this.slimyAppendageDamage = SLIMY_APPENDAGE_DAMAGE;
            this.thrashDamage = THRASH_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.slimyAppendageDamage));
        this.damage.add(new DamageInfo(this, this.thrashDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.degenerateBlock = A17_DEGENERATE_BLOCK;
        }
        else {
            this.degenerateBlock = DEGENERATE_BLOCK;
        }
    }

    @Override
    public void usePreBattleAction() {
        this.gainPowers();
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case SLIMY_APPENDAGE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), this.getRandomAttackEffect()));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, true));
                break;
            case THRASH_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), this.getRandomAttackEffect()));
                break;
            case DEGENERATE_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(this, new DamageInfo(this, this.degenerateHealthLoss, DamageInfo.DamageType.HP_LOSS)));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.degenerateBlock));
                this.gainPowers();
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if ((this.moveHistory.size() >= 2 && !this.lastMove(DEGENERATE_MOVE) && !this.lastMoveBefore(DEGENERATE_MOVE) && (num > 50 || !this.lastMoveBeforeBefore(DEGENERATE_MOVE)))
        ) {
            this.setMove(MOVES[2], DEGENERATE_MOVE, Intent.DEFEND_BUFF);
        }
        else {
            ArrayList<Byte> movesWithoutDegenerate = this.moveHistory.stream().filter(m -> m != DEGENERATE_MOVE).collect(Collectors.toCollection(ArrayList::new));
            byte lastNonDegenerateMove = movesWithoutDegenerate.size() >= 1 ? movesWithoutDegenerate.get(movesWithoutDegenerate.size() - 1) : 0;

            if (!this.firstMove && (lastNonDegenerateMove != THRASH_ATTACK || num < 65)){
                this.setMove(MonstrousExperiment.MOVES[1], THRASH_ATTACK, Intent.ATTACK, this.thrashDamage);
            }
            else {
                this.setMove(MonstrousExperiment.MOVES[0], SLIMY_APPENDAGE_ATTACK, Intent.ATTACK_DEBUFF, this.slimyAppendageDamage);
            }
        }
    }

    private boolean lastMoveBeforeBefore(byte move) {
        return this.moveHistory.size() >= 3 && this.moveHistory.get(this.moveHistory.size() - 3) == move;
    }

    private void gainPowers() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, 1), 1));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this, 1), 1));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, 1), 1));
    }

    private AbstractGameAction.AttackEffect getRandomAttackEffect() {
        List<AbstractGameAction.AttackEffect> attackEffects = Arrays.asList(
            AbstractGameAction.AttackEffect.BLUNT_LIGHT,
            AbstractGameAction.AttackEffect.BLUNT_HEAVY,
            AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
            //AbstractGameAction.AttackEffect.SMASH,
            AbstractGameAction.AttackEffect.SLASH_HEAVY,
            AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
            AbstractGameAction.AttackEffect.SLASH_VERTICAL,
            //AbstractGameAction.AttackEffect.NONE,
            AbstractGameAction.AttackEffect.FIRE,
            AbstractGameAction.AttackEffect.POISON,
            AbstractGameAction.AttackEffect.SHIELD);

        return attackEffects.get(this.random.nextInt(attackEffects.size()));
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = MonstrousExperiment.monsterStrings.NAME;
        MOVES = MonstrousExperiment.monsterStrings.MOVES;
    }
}