package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class CloudVision extends AbstractImageEvent {
    public static final String ID = "Menagerie:CloudVision";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int MAX_HEALTH = 3;
    private static final int A15_MAX_HEALTH = 2;
    private static final int GOLD = 20;
    private static final int A15_GOLD = 15;

    private final int maxHealth;
    private final int gold;
    private int screenNum = 0;
    private boolean pickCard = false;
    private boolean remove = false;

    public CloudVision() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.maxHealth = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH : MAX_HEALTH;
        this.gold = AbstractDungeon.ascensionLevel >= 15 ? A15_GOLD : GOLD;

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.maxHealth));
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.gold));
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            if (this.remove) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
                logMetricRemoveCards(ID, "Wisdom", Collections.singletonList(c.cardID));
            }
            else {
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                AbstractCard transCard = AbstractDungeon.getTransformedCard();
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                logMetric(ID, "Vigor", Collections.singletonList(transCard.cardID), null, Collections.singletonList(c.cardID), null, null, null, null, 0, 0, 0, this.maxHealth, 0, 0);
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Vigor
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.player.increaseMaxHp(this.maxHealth, true);
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            this.pickCard = true;
                            this.remove = false;
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[4], false, true, false, false);
                        }
                        break;
                    case 1: // Cunning
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.player.gainGold(this.gold);
                        AbstractCard card = CardUtil.upgradeRandomCard();
                        List<String> cards = card != null ? Collections.singletonList(card.cardID) : null;
                        logMetric(ID, "Cunning", null, null, null, cards, null, null, null, 0, 0, 0, 0, this.gold, 0);
                        break;
                    case 2: // Wisdom
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            this.pickCard = true;
                            this.remove = true;
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[5], false, false, false, true);
                        }
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }
}
