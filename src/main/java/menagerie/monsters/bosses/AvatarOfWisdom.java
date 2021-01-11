package menagerie.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import menagerie.Menagerie;
import menagerie.powers.WisdomAuraPower;

public class AvatarOfWisdom extends CustomMonster
{
    public static final String ID = "Menagerie:AvatarOfWisdom";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte SHARP_CLAW_ATTACK = 1;
    private static final byte INSIGHT_OF_THE_WISE_BUFF = 2;
    private static final byte OSMOSIS_ATTACK = 3;
    private static final int SHARP_CLAW_DAMAGE = 7;
    private static final int A4_SHARP_CLAW_DAMAGE = 8;
    private static final int INSIGHT_OF_THE_WISE_STRENGTH = 1;
    private static final int OSMOSIS_DAMAGE = 4;
    private static final int A4_OSMOSIS_DAMAGE = 5;
    private static final int OSMOSIS_HEAL = 3;
    private static final int A19_OSMOSIS_HEAL = 5;
    private static final int WISDOM_AURA_THORNS = 2;
    private static final int A19_WISDOM_AURA_THORNS = 3;
    private static final int HP = 75;
    private static final int A9_HP = 80;
    private int sharpClawDamage;
    private int osmosisDamage;
    private int osmosisHeal;
    private int wisdomAuraThorns;

    public AvatarOfWisdom() {
        this(0.0f, 0.0f);
    }

    public AvatarOfWisdom(final float x, final float y) {
        super(AvatarOfWisdom.NAME, ID, HP, -5.0F, 0, 280.0f, 275.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.sharpClawDamage = A4_SHARP_CLAW_DAMAGE;
            this.osmosisDamage = A4_OSMOSIS_DAMAGE;
        } else {
            this.sharpClawDamage = SHARP_CLAW_DAMAGE;
            this.osmosisDamage = OSMOSIS_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.sharpClawDamage));
        this.damage.add(new DamageInfo(this, this.osmosisDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.wisdomAuraThorns = A19_WISDOM_AURA_THORNS;
            this.osmosisHeal = A19_OSMOSIS_HEAL;
        } else {
            this.wisdomAuraThorns = WISDOM_AURA_THORNS;
            this.osmosisHeal = OSMOSIS_HEAL;
        }
    }

    @Override
    public void usePreBattleAction() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        }

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new WisdomAuraPower(this, this.wisdomAuraThorns)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case SHARP_CLAW_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                break;
            case INSIGHT_OF_THE_WISE_BUFF:
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, INSIGHT_OF_THE_WISE_STRENGTH), INSIGHT_OF_THE_WISE_STRENGTH));
                    }
                }
                break;
            case OSMOSIS_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.osmosisHeal));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || this.lastMove(OSMOSIS_ATTACK)) {
            this.setMove(MOVES[0], SHARP_CLAW_ATTACK, Intent.ATTACK, this.sharpClawDamage);
        } else if (this.lastMove(SHARP_CLAW_ATTACK)) {
            this.setMove(MOVES[1], INSIGHT_OF_THE_WISE_BUFF, Intent.BUFF);
        } else {
            this.setMove(MOVES[2], OSMOSIS_ATTACK, Intent.ATTACK_BUFF, this.osmosisDamage);
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
        NAME = AvatarOfWisdom.monsterStrings.NAME;
        MOVES = AvatarOfWisdom.monsterStrings.MOVES;
    }
}