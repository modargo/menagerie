package menagerie.cards.spells;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import menagerie.Menagerie;
import menagerie.actions.HexdrinkerAction;
import menagerie.cards.CustomTags;

public class Hexdrinker extends CustomCard {
    public static final String ID = "Menagerie:Hexdrinker";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int DAMAGE = 4;
    private static final int BLOCK = 4;
    private static final int UPGRADE_DAMAGE = 1;
    private static final int UPGRADE_BLOCK = 1;
    public static final int POISON = 3;
    public static final int ARTIFACT = 1;
    public static final int STATS = 1;

    public Hexdrinker() {
        super(ID, NAME, IMG, -1, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseDamage = DAMAGE;
        this.baseBlock = BLOCK;
        this.tags.add(CustomTags.GRAND_MAGUS_SPELL);
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new HexdrinkerAction(p, m, this.damage, this.block, this.upgraded, this.damageTypeForTurn, this.freeToPlayOnce, this.energyOnUse));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DAMAGE);
            this.upgradeBlock(UPGRADE_BLOCK);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new Hexdrinker();
    }
}
