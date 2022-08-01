package menagerie.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class HarrowingRitual extends AbstractImageEvent {
    public static final String ID = "Menagerie:HarrowingRitual";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int BLOCK_REQUIRED = 7;
    private static final int MAX_HEALTH = 5;
    private static final int A15_MAX_HEALTH = 4;
    private static final int HEALTH_LOSS = 5;
    private static final int A15_HEALTH_LOSS = 7;

    private final int maxHealth;
    private final int healthLoss;
    private int screenNum = 0;

    public HarrowingRitual() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.maxHealth = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH : MAX_HEALTH;
        this.healthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_HEALTH_LOSS : HEALTH_LOSS;

        if (this.hasCardWithEnoughBlock()) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.maxHealth));
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[3], BLOCK_REQUIRED), true);
        }
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healthLoss));
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Shield
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.increaseMaxHp(this.maxHealth, true);
                        logMetricMaxHPGain(ID, "Shield", this.maxHealth);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Accept
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.POISON));
                        AbstractCard card = CardUtil.upgradeRandomCard();
                        List<String> cards = card != null ? Collections.singletonList(card.cardID) : null;
                        logMetric(ID, "Accept", null, null, null, cards, null, null, null, this.healthLoss, 0, 0, 0, 0, 0);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        logMetricIgnored(ID);
                        this.openMap();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }

    private boolean hasCardWithEnoughBlock() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.baseBlock >= BLOCK_REQUIRED) {
                return true;
            }
        }
        return false;
    }
}
