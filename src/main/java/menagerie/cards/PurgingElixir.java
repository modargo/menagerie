package menagerie.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import menagerie.Menagerie;

public class PurgingElixir extends CustomCard {
    public static final String ID = "Menagerie:PurgingElixir";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 0;
    private static final int HEALTH_LOSS = 5;
    private static final int UPGRADE_HEALTH = -2;

    public PurgingElixir() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = HEALTH_LOSS;
        this.magicNumber = this.baseMagicNumber;
        this.selfRetain = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.FAST_MODE) {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.5F));
        }

        this.addToBot(new LoseHPAction(p, p, HEALTH_LOSS));
        if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID) && AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount < 0) {
            this.addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, StrengthPower.POWER_ID));
        }
        if (AbstractDungeon.player.hasPower(DexterityPower.POWER_ID) && AbstractDungeon.player.getPower(DexterityPower.POWER_ID).amount < 0) {
            this.addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, DexterityPower.POWER_ID));
        }
        if (AbstractDungeon.player.hasPower(ConstrictedPower.POWER_ID)) {
            this.addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, ConstrictedPower.POWER_ID));
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_HEALTH);
        }
    }

    public AbstractCard makeCopy() {
        return new PurgingElixir();
    }
}
