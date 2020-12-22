package menagerie.cards.spells;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import menagerie.Menagerie;
import menagerie.actions.CardTypeDiscardPileToHandAction;
import menagerie.actions.SuddenReclamationMillAction;
import menagerie.cards.CustomTags;

public class SuddenReclamation extends CustomCard {
    public static final String ID = "Menagerie:SuddenReclamation";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;
    private static final int MILL = 4;

    public SuddenReclamation() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = MILL;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.GRAND_MAGUS_SPELL);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SuddenReclamationMillAction(this.magicNumber));
        this.addToBot(new CardTypeDiscardPileToHandAction(1, false, CardType.ATTACK));
        this.addToBot(new CardTypeDiscardPileToHandAction(1, false, CardType.SKILL));
        this.addToBot(new CardTypeDiscardPileToHandAction(1, false, CardType.POWER));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
        }
    }

    public AbstractCard makeCopy() {
        return new SuddenReclamation();
    }
}
