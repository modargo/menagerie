package menagerie.cards.spells;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import menagerie.Menagerie;
import menagerie.actions.ExhaustCardInDiscardPileAction;
import menagerie.cards.CustomTags;

public class RelicOfProgenitus extends CustomCard {
    public static final String ID = "Menagerie:RelicOfProgenitus";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int UPGRADE_COST = 0;
    private static final int DRAW = 1;
    private static final int CARDS_TO_EXHAUST = 1;

    public RelicOfProgenitus() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = DRAW;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.GRAND_MAGUS_SPELL);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            this.addToBot(new ExhaustSpecificCardAction(c, AbstractDungeon.player.discardPile, true));
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
        }
    }

    @Override
    public void triggerWhenDrawn() {
        this.addToBot(new ExhaustCardInDiscardPileAction(CARDS_TO_EXHAUST, true));
    }

    public AbstractCard makeCopy() {
        return new RelicOfProgenitus();
    }
}
