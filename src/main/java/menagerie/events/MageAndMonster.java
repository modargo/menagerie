package menagerie.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import menagerie.Menagerie;
import menagerie.cards.SagesJudgement;
import menagerie.cards.Slaughter;

import java.text.MessageFormat;

public class MageAndMonster extends AbstractImageEvent {
    public static final String ID = "Menagerie:MageAndMonster";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int HEALTH_LOSS = 5;
    private static final int A15_HEALTH_LOSS = 7;
    private static final int GOLD = 15;
    private static final int A15_GOLD = 20;

    private int healthLoss;
    private int gold;
    private AbstractCard mageCard;
    private AbstractCard monsterCard;
    private int screenNum = 0;

    public MageAndMonster() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.mageCard = new SagesJudgement();
        this.monsterCard = new Slaughter();
        this.healthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_HEALTH_LOSS : HEALTH_LOSS;
        this.gold = AbstractDungeon.ascensionLevel >= 15 ? A15_GOLD : GOLD;

        imageEventText.setDialogOption(OPTIONS[0].replace("{0}", this.healthLoss + ""), this.mageCard);
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healthLoss), this.monsterCard);
        imageEventText.setDialogOption(OPTIONS[2].replace("{0}", this.gold + ""));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Help the mage
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.mageCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        logMetricObtainCardAndDamage(ID, "Help the mage", this.mageCard, this.healthLoss);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Help the monster
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.monsterCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        logMetricObtainCardAndDamage(ID, "Help the mage", this.monsterCard, this.healthLoss);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Avoid the battle
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.player.loseGold(this.gold);
                        logMetricLoseGold(ID, "Avoid the battle", this.gold);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }
}
