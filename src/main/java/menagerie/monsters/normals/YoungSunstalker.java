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
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import menagerie.Menagerie;

public class YoungSunstalker extends CustomMonster
{
    public static final String ID = "Menagerie:YoungSunstalker";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte TENTACLE_SLAP_ATTACK = 1;
    private static final byte SUNBATHE_MOVE = 2;
    private static final int TENTACLE_SLAP_DAMAGE = 2;
    private static final int A2_TENTACLE_SLAP_DAMAGE = 3;
    private static final int TENTACLE_SLAP_HITS = 3;
    private static final int SUNBATHE_BLOCK = 8;
    private static final int A7_SUNBATHE_BLOCK = 12;
    private static final int SUNBATHE_PLATED_ARMOR = 2;
    private static final int A17_SUNBATHE_PLATED_ARMOR = 3;
    private static final int HP_MIN = 47;
    private static final int HP_MAX = 52;
    private static final int A7_HP_MIN = 51;
    private static final int A7_HP_MAX = 56;
    private int tentacleSlapDamage;
    private int sunbatheBlock;
    private int sunbathePlatedArmor;

    public YoungSunstalker() {
        this(0.0f, 0.0f);
    }

    public YoungSunstalker(final float x, final float y) {
        super(YoungSunstalker.NAME, ID, HP_MAX, -5.0F, 0, 305.0f, 145.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.sunbatheBlock = A7_SUNBATHE_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.sunbatheBlock = SUNBATHE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.tentacleSlapDamage = A2_TENTACLE_SLAP_DAMAGE;
        } else {
            this.tentacleSlapDamage = TENTACLE_SLAP_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tentacleSlapDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.sunbathePlatedArmor = A17_SUNBATHE_PLATED_ARMOR;
        } else {
            this.sunbathePlatedArmor = SUNBATHE_PLATED_ARMOR;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case TENTACLE_SLAP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < TENTACLE_SLAP_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;
            case SUNBATHE_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this.sunbatheBlock));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new PlatedArmorPower(m, this.sunbathePlatedArmor), this.sunbathePlatedArmor));
                    }
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || this.lastMove(SUNBATHE_MOVE)) {
            this.setMove(MOVES[0], TENTACLE_SLAP_ATTACK, Intent.ATTACK, this.tentacleSlapDamage, TENTACLE_SLAP_HITS, true);
        }
        else {
            this.setMove(MOVES[1], SUNBATHE_MOVE, Intent.DEFEND);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = YoungSunstalker.monsterStrings.NAME;
        MOVES = YoungSunstalker.monsterStrings.MOVES;
    }
}