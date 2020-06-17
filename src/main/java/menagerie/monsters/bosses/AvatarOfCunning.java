package menagerie.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import menagerie.Menagerie;
import menagerie.powers.CunningAuraPower;

public class AvatarOfCunning extends CustomMonster
{
    public static final String ID = "Menagerie:AvatarOfCunning";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte FINESSE_OF_THE_CUNNING_ATTACK = 1;
    private static final byte ASSAIL_ATTACK = 2;
    private static final int FINESSE_OF_THE_CUNNING_DAMAGE = 1;
    private static final int A4_FINESSE_OF_THE_CUNNING_DAMAGE = 2;
    private static final int FINESSE_OF_THE_CUNNING_HITS = 2;
    private static final int A19_FINESSE_OF_THE_CUNNING_HITS = 3;
    private static final int ASSAIL_DAMAGE = 8;
    private static final int A4_ASSAIL_DAMAGE = 9;
    private static final int ASSAIL_BLOCK = 4;
    private static final int CUNNING_AURA_PLATED_ARMOR = 2;
    private static final int A19_CUNNING_AURA_PLATED_ARMOR = 4;
    private static final int HP = 80;
    private static final int A9_HP = 90;
    private int finesseOfTheCunningDamage;
    private int finesseOfTheCunningHits;
    private int assailDamage;
    private int cunningAuraPlatedArmor;

    public AvatarOfCunning() {
        this(0.0f, 0.0f);
    }

    public AvatarOfCunning(final float x, final float y) {
        super(AvatarOfCunning.NAME, ID, HP, -5.0F, 0, 280.0f, 275.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.finesseOfTheCunningDamage = A4_FINESSE_OF_THE_CUNNING_DAMAGE;
            this.assailDamage = A4_ASSAIL_DAMAGE;
        } else {
            this.finesseOfTheCunningDamage = FINESSE_OF_THE_CUNNING_DAMAGE;
            this.assailDamage = ASSAIL_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.finesseOfTheCunningDamage));
        this.damage.add(new DamageInfo(this, this.assailDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.finesseOfTheCunningHits = A19_FINESSE_OF_THE_CUNNING_HITS;
            this.cunningAuraPlatedArmor = A19_CUNNING_AURA_PLATED_ARMOR;
        } else {
            this.finesseOfTheCunningHits = FINESSE_OF_THE_CUNNING_HITS;
            this.cunningAuraPlatedArmor = CUNNING_AURA_PLATED_ARMOR;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CunningAuraPower(this, this.cunningAuraPlatedArmor)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case FINESSE_OF_THE_CUNNING_ATTACK:
                for (int i = 0; i < this.finesseOfTheCunningHits; i++) {
                    AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                }
                break;
            case ASSAIL_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, ASSAIL_BLOCK));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || this.lastMove(ASSAIL_ATTACK)) {
            this.setMove(MOVES[0], FINESSE_OF_THE_CUNNING_ATTACK, Intent.ATTACK, this.finesseOfTheCunningDamage, this.finesseOfTheCunningHits, true);
        } else {
            this.setMove(MOVES[1], ASSAIL_ATTACK, Intent.ATTACK_DEFEND, this.assailDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = AvatarOfCunning.monsterStrings.NAME;
        MOVES = AvatarOfCunning.monsterStrings.MOVES;
    }
}