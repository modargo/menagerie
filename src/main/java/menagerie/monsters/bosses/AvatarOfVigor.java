package menagerie.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
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
import menagerie.powers.VigorAuraPower;

public class AvatarOfVigor extends CustomMonster
{
    public static final String ID = "Menagerie:AvatarOfVigor";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte ENDURANCE_OF_THE_VIGOROUS_MOVE = 1;
    private static final byte ASSAULT_ATTACK = 2;
    private static final byte BODY_SLAM_ATTACK = 3;
    private static final int ENDURANCE_OF_THE_VIGOROUS_BLOCK = 0;
    private static final int A9_ENDURANCE_OF_THE_VIGOROUS_BLOCK = 0;
    private static final int ENDURANCE_OF_THE_VIGOROUS_PLATED_ARMOR = 1;
    private static final int A19_ENDURANCE_OF_THE_VIGOROUS_PLATED_ARMOR = 2;
    private static final int ASSAULT_DAMAGE = 4;
    private static final int A4_ASSAULT_DAMAGE = 5;
    private static final int ASSAULT_HITS = 2;
    private static final int BODY_SLAM_DAMAGE = 9;
    private static final int A4_BODY_SLAM_DAMAGE = 10;
    private static final int VIGOR_AURA_VULNERABLE = 2;
    private static final int A19_VIGOR_AURA_VULNERABLE = 3;
    private static final int HP = 75;
    private static final int A9_HP = 80;
    private int enduranceOfTheVigorousBlock;
    private int enduranceOfTheVigorousPlatedArmor;
    private int assaultDamage;
    private int bodySlamDamage;
    private int vigorAuraVulnerable;

    public AvatarOfVigor() {
        this(0.0f, 0.0f);
    }

    public AvatarOfVigor(final float x, final float y) {
        super(AvatarOfVigor.NAME, ID, HP, -5.0F, 0, 280.0f, 275.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
            this.enduranceOfTheVigorousBlock = A9_ENDURANCE_OF_THE_VIGOROUS_BLOCK;
        } else {
            this.setHp(HP);
            this.enduranceOfTheVigorousBlock = ENDURANCE_OF_THE_VIGOROUS_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.assaultDamage = A4_ASSAULT_DAMAGE;
            this.bodySlamDamage = A4_BODY_SLAM_DAMAGE;
        } else {
            this.assaultDamage = ASSAULT_DAMAGE;
            this.bodySlamDamage = BODY_SLAM_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.assaultDamage));
        this.damage.add(new DamageInfo(this, this.bodySlamDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.vigorAuraVulnerable = A19_VIGOR_AURA_VULNERABLE;
            this.enduranceOfTheVigorousPlatedArmor = A19_ENDURANCE_OF_THE_VIGOROUS_PLATED_ARMOR;
        } else {
            this.vigorAuraVulnerable = VIGOR_AURA_VULNERABLE;
            this.enduranceOfTheVigorousPlatedArmor = ENDURANCE_OF_THE_VIGOROUS_PLATED_ARMOR;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new VigorAuraPower(this, this.vigorAuraVulnerable)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case ENDURANCE_OF_THE_VIGOROUS_MOVE:
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || !m.isDying) {
                        if (this.enduranceOfTheVigorousBlock > 0) {
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.enduranceOfTheVigorousBlock));
                        }
                        if (this.enduranceOfTheVigorousPlatedArmor > 0) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new PlatedArmorPower(m, this.enduranceOfTheVigorousPlatedArmor), this.enduranceOfTheVigorousPlatedArmor));
                        }
                    }
                }
                break;
            case ASSAULT_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case BODY_SLAM_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || this.lastMove(BODY_SLAM_ATTACK)) {
            this.setMove(MOVES[0], ENDURANCE_OF_THE_VIGOROUS_MOVE, Intent.DEFEND);
        } else if (this.lastMove(ENDURANCE_OF_THE_VIGOROUS_MOVE)) {
            this.setMove(MOVES[1], ASSAULT_ATTACK, Intent.ATTACK, this.assaultDamage, ASSAULT_HITS, true);
        } else {
            this.setMove(MOVES[2], BODY_SLAM_ATTACK, Intent.ATTACK, this.bodySlamDamage);
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

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = AvatarOfVigor.monsterStrings.NAME;
        MOVES = AvatarOfVigor.monsterStrings.MOVES;
    }
}