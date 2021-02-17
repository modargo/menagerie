package menagerie.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import menagerie.Menagerie;

public class BloodySpellbook extends CustomCard {
    public static final String ID = "Menagerie:BloodySpellbook";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = -2;
    private static final int DAMAGE = 5;

    public BloodySpellbook() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
        this.baseMagicNumber = DAMAGE;
        this.magicNumber = this.baseMagicNumber;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (c.hasTag(CustomTags.GRAND_MAGUS_SPELL)) {
            this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, this.magicNumber));
        }
    }

    @Override
    public void upgrade() {}

    public AbstractCard makeCopy() {
        return new BloodySpellbook();
    }
}
