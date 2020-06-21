package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import menagerie.Menagerie;

public class KeeperOfTheSacredPool extends CustomMonster
{
    public static final String ID = "Menagerie:KeeperOfTheSacredPool";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte INVOKE_THE_SUN_BUFF = 1;
    private static final byte TRIPLE_BLAST_ATTACK = 2;
    private static final int TRIPLE_BLAST_DAMAGE = 3;
    private static final int A2_TRIPLE_BLAST_DAMAGE = 4;
    private static final int TRIPLE_BLAST_HITS = 3;
    private static final int INVOKE_THE_SUN_STRENGTH = 1;
    private static final int A17_INVOKE_THE_SUN_STRENGTH = 1;
    private static final int HP_MIN = 47;
    private static final int HP_MAX = 52;
    private static final int A7_HP_MIN = 51;
    private static final int A7_HP_MAX = 56;
    private int tripleBlastDamage;
    private int invokeTheSunStrength;

    public KeeperOfTheSacredPool() {
        this(0.0f, 0.0f);
    }

    public KeeperOfTheSacredPool(final float x, final float y) {
        super(KeeperOfTheSacredPool.NAME, ID, HP_MAX, -5.0F, 0, 245.0f, 285.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.tripleBlastDamage = A2_TRIPLE_BLAST_DAMAGE;
        } else {
            this.tripleBlastDamage = TRIPLE_BLAST_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tripleBlastDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.invokeTheSunStrength = A17_INVOKE_THE_SUN_STRENGTH;
        } else {
            this.invokeTheSunStrength = INVOKE_THE_SUN_STRENGTH;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case INVOKE_THE_SUN_BUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.invokeTheSunStrength), this.invokeTheSunStrength));
                    }
                }
                break;
            case TRIPLE_BLAST_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                this.addToBot(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY), Settings.FAST_MODE ? 0.0F : 0.1F));
                this.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.POISON));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || this.lastMove(TRIPLE_BLAST_ATTACK)) {
            this.setMove(MOVES[0], INVOKE_THE_SUN_BUFF, Intent.BUFF);
        }
        else {
            this.setMove(MOVES[1], TRIPLE_BLAST_ATTACK, Intent.ATTACK, this.tripleBlastDamage, TRIPLE_BLAST_HITS, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = KeeperOfTheSacredPool.monsterStrings.NAME;
        MOVES = KeeperOfTheSacredPool.monsterStrings.MOVES;
    }
}