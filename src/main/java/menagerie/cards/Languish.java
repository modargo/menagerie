package menagerie.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import menagerie.Menagerie;

public class Languish extends CustomCard {
    public static final String ID = "Menagerie:Languish";
    public static final String IMG = Menagerie.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 0;
    private static final int EFFECT = 4;
    private static final int UPGRADE_EFFECT = 2;

    public Languish() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ALL_ENEMY);
        this.baseMagicNumber = EFFECT;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.GRAND_MAGUS_SPELL);
    }

    public void use(AbstractPlayer p, AbstractMonster monster) {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            this.addToBot(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.magicNumber, DamageInfo.DamageType.HP_LOSS)));
            this.addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -this.magicNumber), -this.magicNumber));
            if (m != null && !m.hasPower("Artifact")) {
                this.addToBot(new ApplyPowerAction(m, p, new GainStrengthPower(m, this.magicNumber), this.magicNumber));
            }
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_EFFECT);
        }
    }

    public AbstractCard makeCopy() {
        return new Languish();
    }
}
