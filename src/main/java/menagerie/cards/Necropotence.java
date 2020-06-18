package menagerie.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;
import menagerie.Menagerie;

public class Necropotence extends CustomCard {
    public static final String ID = "Menagerie:Necropotence";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 0;
    private static final int DRAW = 1;
    private static final int UPGRADE_DRAW = 1;
    private static final int HEALTH_LOSS = 1;

    public Necropotence() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = DRAW;
        this.magicNumber = this.baseMagicNumber;
        this.selfRetain = true;
        this.returnToHand = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new VerticalAuraEffect(Color.BLACK, p.hb.cX, p.hb.cY), 0.33F));
        this.addToBot(new VFXAction(p, new BorderLongFlashEffect(Color.MAGENTA), 0.0F, true));
        this.addToBot(new DrawCardAction(p, this.magicNumber));
        this.addToBot(new LoseHPAction(p, p, HEALTH_LOSS));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_DRAW);
            this.rawDescription = UPGRADE_DESCRIPTION;
        }
    }

    public AbstractCard makeCopy() {
        return new Dismember();
    }
}
