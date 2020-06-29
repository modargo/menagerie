package menagerie.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.RoomTintEffect;
import menagerie.Menagerie;
import menagerie.effects.FireEffect;
import menagerie.effects.SmallColorLaserEffect;
import menagerie.powers.SolarChargePower;

public class Sunstalker extends CustomMonster
{
    public static final String ID = "Menagerie:Sunstalker";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Menagerie.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte TENTACLE_SLAP_ATTACK = 1;
    private static final byte BEAM_ATTACK = 2;
    private static final byte SOLAR_ENERGY = 3;
    private static final byte SOLAR_FLARE = 4;
    private static final int TENTACLE_SLAP_DAMAGE = 1;
    private static final int A4_TENTACLE_SLAP_DAMAGE = 2;
    private static final int TENTACLE_SLAP_HITS = 2;
    private static final int BEAM_DAMAGE = 5;
    private static final int A4_BEAM_DAMAGE = 6;
    private static final int BEAM_CHARGES = 1;
    private static final int A19_BEAM_CHARGES = 1;
    private static final int SOLAR_ENERGY_CHARGES = 2;
    private static final int A19_SOLAR_ENERGY_CHARGES = 3;
    private static final int FLARE_DAMAGE = 11;
    private static final int A4_FLARE_DAMAGE = 13;
    private static final int FLARE_DEBUFFS = 1;
    private static final int FLARE_BURNS = 1;
    private static final int HP = 175;
    private static final int A9_HP = 190;
    private int tentacleSlapDamage;
    private int beamDamage;
    private int beamCharges;
    private int solarEnergyCharges;
    private int flareDamage;

    private SolarChargePower power;

    public Sunstalker() {
        this(0.0f, 0.0f);
    }

    public Sunstalker(final float x, final float y) {
        super(Sunstalker.NAME, ID, HP, -5.0F, 0, 555.0f, 415.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.tentacleSlapDamage = A4_TENTACLE_SLAP_DAMAGE;
            this.beamDamage = A4_BEAM_DAMAGE;
            this.flareDamage = A4_FLARE_DAMAGE;
        } else {
            this.tentacleSlapDamage = TENTACLE_SLAP_DAMAGE;
            this.beamDamage = BEAM_DAMAGE;
            this.flareDamage = FLARE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tentacleSlapDamage));
        this.damage.add(new DamageInfo(this, this.beamDamage));
        this.damage.add(new DamageInfo(this, this.flareDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.solarEnergyCharges = A19_SOLAR_ENERGY_CHARGES;
            this.beamCharges = A19_BEAM_CHARGES;
        } else {
            this.solarEnergyCharges = SOLAR_ENERGY_CHARGES;
            this.beamCharges = BEAM_CHARGES;
        }
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");

        this.power = new SolarChargePower(this, 0);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, this.power));
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
            case BEAM_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.RED)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmallColorLaserEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.hb.cX, this.hb.cY, Color.RED), Settings.FAST_MODE ? 0.1F : 0.3F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.NONE, Settings.FAST_MODE));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SolarChargePower(this, this.beamCharges), this.beamCharges));
                break;
            case SOLAR_ENERGY:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(AbstractDungeon.player, 0.6F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.ORANGE)));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SolarChargePower(this, this.solarEnergyCharges), this.solarEnergyCharges));
                break;
            case SOLAR_FLARE:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new RoomTintEffect(Color.RED, 0.5F)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.power.amount * 2), 0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, FLARE_DEBUFFS, true), FLARE_DEBUFFS));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, FLARE_DEBUFFS, true), FLARE_DEBUFFS));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, FLARE_DEBUFFS, true), FLARE_DEBUFFS));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), FLARE_BURNS));
                AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, SolarChargePower.POWER_ID, SolarChargePower.CHARGES_FOR_FLARE));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int tentacleSlapCount = 0;
        int beamCount = 0;
        for (byte b : this.moveHistory) {
            switch (b) {
                case TENTACLE_SLAP_ATTACK:
                    tentacleSlapCount++;
                    break;
                case BEAM_ATTACK:
                    beamCount++;
                    break;
            }
        }

        int solarEnergyChance;
        if (this.lastMove(SOLAR_ENERGY) || this.moveHistory.size() == 0) {
            solarEnergyChance = 0;
        }
        else if (this.lastMoveBefore(SOLAR_ENERGY) || this.moveHistory.size() == 1) {
            solarEnergyChance = 25;
        }
        else if (this.lastMoveX(SOLAR_ENERGY, 3) || this.moveHistory.size() == 2) {
            solarEnergyChance = 50;
        }
        else if (this.lastMoveX(SOLAR_ENERGY, 4) || this.moveHistory.size() == 3) {
            solarEnergyChance = 75;
        }
        else if (this.lastMoveX(SOLAR_ENERGY, 5) || this.moveHistory.size() == 4) {
            solarEnergyChance = 100;
        }
        else {
            solarEnergyChance = 100;
        }
        if (this.power != null && this.power.amount >= SolarChargePower.CHARGES_FOR_FLARE) {
            this.setMove(MOVES[3], SOLAR_FLARE, Intent.ATTACK_DEBUFF, this.flareDamage);
        }
        else if (!this.lastMove(SOLAR_FLARE) && num < solarEnergyChance) {
            this.setMove(MOVES[2], SOLAR_ENERGY, Intent.MAGIC);
        }
        else {
            int extraTentacleSlaps = tentacleSlapCount - beamCount;
            if (this.firstMove || num < 50 + (extraTentacleSlaps * 25)) {
                this.setMove(MOVES[1], BEAM_ATTACK, Intent.ATTACK_BUFF, this.beamDamage);
            }
            else {
                this.setMove(MOVES[0], TENTACLE_SLAP_ATTACK, Intent.ATTACK, this.tentacleSlapDamage, TENTACLE_SLAP_HITS, true);
            }
        }
    }

    private boolean lastMoveX(byte move, int movesAgo) {
        return this.moveHistory.size() >= movesAgo && this.moveHistory.get(this.moveHistory.size() - movesAgo) == move;
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
        NAME = Sunstalker.monsterStrings.NAME;
        MOVES = Sunstalker.monsterStrings.MOVES;
    }
}