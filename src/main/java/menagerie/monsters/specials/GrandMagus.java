package menagerie.monsters.specials;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.RoomTintEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import menagerie.Menagerie;
import menagerie.effects.DarkblastEffect;
import menagerie.effects.FireEffect;
import menagerie.powers.SulfuricVortexPower;

public class GrandMagus extends CustomMonster
{
    public static final String ID = "Menagerie:GrandMagus";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte MIND_TWIST_DEBUFF = 1;
    private static final byte LIGHTNING_BOLT_ATTACK = 2;
    private static final byte DARKBLAST_ATTACK = 3;
    private static final byte SULFURIC_VORTEX_DEBUFF = 4;
    private static final int MIND_TWIST_AMOUNT = 2;
    private static final int A18_MIND_TWIST_AMOUNT = 3;
    private static final int LIGHTNING_BOLT_DAMAGE = 10;
    private static final int A3_LIGHTNING_BOLT_DAMAGE = 12;
    private static final int DARKBLAST_DAMAGE = 2;
    private static final int A3_DARKBLAST_DAMAGE = 2;
    private static final int DARKBLAST_STATS = 1;
    private static final int SULFURIC_VORTEX_AMOUNT = 2;
    private static final int A18_SULFURIC_VORTEX_AMOUNT = 3;
    private static final int HP_MIN = 141;
    private static final int HP_MAX = 145;
    private static final int A8_HP_MIN = 144;
    private static final int A8_HP_MAX = 149;

    private int mindTwistAmount;
    private int lightningBoltDamage;
    private int darkBlastDamage;
    private int sulfuricVortexAmount;

    public GrandMagus() {
        this(0.0f, 0.0f);
    }

    public GrandMagus(final float x, final float y) {
        super(GrandMagus.NAME, ID, HP_MAX, -5.0F, 0, 280.0f, 365.0f, IMG, x, y);
        this.type = EnemyType.ELITE;
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(A8_HP_MIN, A8_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.lightningBoltDamage = A3_LIGHTNING_BOLT_DAMAGE;
            this.darkBlastDamage = A3_DARKBLAST_DAMAGE;
        } else {
            this.lightningBoltDamage = LIGHTNING_BOLT_DAMAGE;
            this.darkBlastDamage = DARKBLAST_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.lightningBoltDamage));
        this.damage.add(new DamageInfo(this, this.darkBlastDamage));

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.mindTwistAmount = A18_MIND_TWIST_AMOUNT;
            this.sulfuricVortexAmount = A18_SULFURIC_VORTEX_AMOUNT;
        } else {
            this.lightningBoltDamage = MIND_TWIST_AMOUNT;
            this.darkBlastDamage = SULFURIC_VORTEX_AMOUNT;
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case MIND_TWIST_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.BLACK, ShockWaveEffect.ShockWaveType.CHAOTIC), Settings.FAST_MODE ? 0.25F : 0.75F));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), this.mindTwistAmount, false, true));
                break;
            case LIGHTNING_BOLT_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                this.addToBot(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY), Settings.FAST_MODE ? 0.0F : 0.1F));
                this.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                break;
            case DARKBLAST_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new DarkblastEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.6F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.NONE));
                this.addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -DARKBLAST_STATS), -DARKBLAST_STATS));
                this.addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new DexterityPower(AbstractDungeon.player, -DARKBLAST_STATS), -DARKBLAST_STATS));
                break;
            case SULFURIC_VORTEX_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, 0), 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new SulfuricVortexPower(AbstractDungeon.player, this, this.sulfuricVortexAmount), this.sulfuricVortexAmount));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int mindTwistCount = 0;
        int lightningBoltCount = 0;
        int darkblastCount = 0;
        int sulfuricVortexCount = 0;
        for (byte b : this.moveHistory) {
            switch (b) {
                case MIND_TWIST_DEBUFF:
                    mindTwistCount++;
                    break;
                case LIGHTNING_BOLT_ATTACK:
                    lightningBoltCount++;
                    break;
                case DARKBLAST_ATTACK:
                    darkblastCount++;
                    break;
                case SULFURIC_VORTEX_DEBUFF:
                    sulfuricVortexCount++;
                    break;
            }
        }
        if (this.lastMove(MIND_TWIST_DEBUFF) || this.lastMove(SULFURIC_VORTEX_DEBUFF)) {
            int extraLightningBolts = lightningBoltCount - darkblastCount;
            if (extraLightningBolts > 0 || (extraLightningBolts == 0 && num < 50)){
                this.setMove(MOVES[2], DARKBLAST_ATTACK, Intent.ATTACK_DEBUFF, this.darkBlastDamage);
            }
            else {
                this.setMove(MOVES[1], LIGHTNING_BOLT_ATTACK, Intent.ATTACK, this.lightningBoltDamage);
            }
        } else {
            int extraMindTwists = mindTwistCount - sulfuricVortexCount;
            if (extraMindTwists > 0 || (extraMindTwists == 0 && num < 50)){
                this.setMove(MOVES[3], SULFURIC_VORTEX_DEBUFF, Intent.DEBUFF);
            }
            else {
                this.setMove(MOVES[0], MIND_TWIST_DEBUFF, Intent.STRONG_DEBUFF);
            }
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GrandMagus.monsterStrings.NAME;
        MOVES = GrandMagus.monsterStrings.MOVES;
    }
}