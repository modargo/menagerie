package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ThreeAnimals extends AbstractImageEvent {
    public static final String ID = "Menagerie:ThreeAnimals";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final float HEAL_PERCENT = 0.25f;
    private static final float A15_HEAL_PERCENT = 0.20f;

    private final int healAmount;
    private int screenNum = 0;

    public ThreeAnimals() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;

        float healPercent = AbstractDungeon.ascensionLevel >= 15 ? A15_HEAL_PERCENT : HEAL_PERCENT;
        this.healAmount = (int)(AbstractDungeon.player.maxHealth * healPercent);

        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healAmount));
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            logMetricCardRemoval(ID, "Snake", c);
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Lion
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.showCardReward(1);
                        logMetric(ID, "Lion");
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Goat
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.heal(this.healAmount, true);
                        logMetricHeal(ID, "Goat", this.healAmount);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Snake
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[4], false, false, false, true);
                        }
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void showCardReward(int numRewards) {
        AbstractDungeon.getCurrRoom().rewards.clear();
        for(int i = 0; i < numRewards; ++i) {
            RewardItem reward = new RewardItem();
            ArrayList<AbstractCard> cards = new ArrayList<>();
            //We see what was already generated and use that, to avoid advancing the rare counter further
            for (AbstractCard c : reward.cards) {
                AbstractCard.CardRarity rarity = c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE ? c.rarity : AbstractCard.CardRarity.COMMON;
                AbstractCard card = null;
                while (card == null) {
                    AbstractCard potentialCard = CardUtil.getOtherColorCard(rarity, Arrays.asList(AbstractDungeon.player.getCardColor(), AbstractCard.CardColor.COLORLESS));
                    if (cards.stream().noneMatch(c1 -> c1.cardID.equals(potentialCard.cardID))) {
                        card = potentialCard;
                    }
                }
                cards.add(card);
            }
            reward.cards = cards;
            AbstractDungeon.getCurrRoom().addCardReward(reward);
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
}
