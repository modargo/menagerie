package menagerie.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import menagerie.Menagerie;

public class RedMage extends CustomMonster
{
    public static final String ID = "Menagerie:RedMage";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte MUDDLE_DEBUFF = 1;
    private static final byte RIPOSTE_ATTACK = 2;
    private static final byte BOLT_ATTACK = 3;
    private static final int MUDDLE_FRAIL = 1;
    private static final int A17_MUDDLE_FRAIL = 2;
    private static final int RIPOSTE_DAMAGE = 8;
    private static final int A2_RIPOSTE_DAMAGE = 10;
    private static final int RIPOSTE_BLOCK = 6;
    private static final int A7_RIPOSTE_BLOCK = 8;
    private static final int BOLT_DAMAGE = 6;
    private static final int A2_BOLT_DAMAGE = 7;
    private static final int BOLT_HITS = 2;
    private static final int HP_MIN = 61;
    private static final int HP_MAX = 63;
    private static final int A7_HP_MIN = 62;
    private static final int A7_HP_MAX = 64;
    private int muddleFrail;
    private int riposteDamage;
    private int riposteBlock;
    private int boltDamage;

    public RedMage() {
        this(0.0f, 0.0f);
    }

    public RedMage(final float x, final float y) {
        super(RedMage.NAME, ID, HP_MAX, -5.0F, 0, 280.0f, 355.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.riposteBlock = A7_RIPOSTE_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.riposteBlock = RIPOSTE_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.riposteDamage = A2_RIPOSTE_DAMAGE;
            this.boltDamage = A2_BOLT_DAMAGE;
        } else {
            this.riposteDamage = RIPOSTE_DAMAGE;
            this.boltDamage = BOLT_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.riposteDamage));
        this.damage.add(new DamageInfo(this, this.boltDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.muddleFrail = A17_MUDDLE_FRAIL;
        } else {
            this.muddleFrail = MUDDLE_FRAIL;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case MUDDLE_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.muddleFrail, true), this.muddleFrail));
                break;
            case RIPOSTE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.riposteBlock));
                break;
            case BOLT_ATTACK:
                for (int i = 0; i < BOLT_HITS; i++) {
                    this.addToBot(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY), Settings.FAST_MODE ? 0.0F : 0.1F));
                    this.addToBot(new SFXAction("ORB_LIGHTNING_EVOKE"));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.NONE));
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove || (num < 35 && !this.lastMove(MUDDLE_DEBUFF) && !this.lastMove(MUDDLE_DEBUFF))) {
            this.setMove(MOVES[0], MUDDLE_DEBUFF, Intent.DEBUFF);
        }
        else if ((!this.lastMove(RIPOSTE_ATTACK) || !this.lastMoveBefore(RIPOSTE_ATTACK)) && (this.lastMove(BOLT_ATTACK) || num < 50)){
            this.setMove(MOVES[1], RIPOSTE_ATTACK, Intent.ATTACK_DEFEND, this.riposteDamage);
        }
        else {
            this.setMove(MOVES[2], BOLT_ATTACK, Intent.ATTACK, this.boltDamage, BOLT_HITS, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = RedMage.monsterStrings.NAME;
        MOVES = RedMage.monsterStrings.MOVES;
    }
}