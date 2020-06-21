package menagerie.cards.spells;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;
import menagerie.cards.CustomTags;

public class Skullclamp extends CustomCard {
    public static final String ID = "Menagerie:Skullclamp";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;
    private static final int STATS = 1;

    public Skullclamp() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = STATS;
        this.magicNumber = this.baseMagicNumber;
        this.isEthereal = true;
        this.tags.add(CustomTags.GRAND_MAGUS_SPELL);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -this.magicNumber), -this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new DrawPower(p, 1), 1));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.isEthereal = false;
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new Skullclamp();
    }
}
