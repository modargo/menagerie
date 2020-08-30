package menagerie.events;

import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;
import java.util.Collections;

public class MagicPeddler extends AbstractImageEvent {
    public static final String ID = "Menagerie:MagicPeddler";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    public static final int POTION_COST = 15;
    public static final int A15_POTION_COST = 20;
    private static final int HEAL_COST = 30;
    private static final int A15_HEAL_COST = 35;
    private static final int SPELLS_COST = 70;
    private static final int A15_SPELLS_COST = 80;
    private static final float HEAL_PERCENT = 0.25f;
    private static final int SPELLS = 2;

    private int potionCost;
    private int healCost;
    private int spellsCost;
    private int healAmount;
    private int screenNum = 0;

    public MagicPeddler() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;

        this.potionCost = AbstractDungeon.ascensionLevel >= 15 ? A15_POTION_COST : POTION_COST;
        this.healCost = AbstractDungeon.ascensionLevel >= 15 ? A15_HEAL_COST : HEAL_COST;
        this.spellsCost = AbstractDungeon.ascensionLevel >= 15 ? A15_SPELLS_COST : SPELLS_COST;
        this.healAmount = (int)(AbstractDungeon.player.maxHealth * HEAL_PERCENT);

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.potionCost));
        if (AbstractDungeon.player.gold >= this.healCost) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healCost, this.healAmount));
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.healCost), true);
        }
        if (AbstractDungeon.player.gold >= this.spellsCost) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.spellsCost, SPELLS));
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.spellsCost), true);
        }
        imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Potion
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(this.potionCost);
                        AbstractPotion potion = PotionHelper.getRandomPotion();
                        new ObtainPotionAction(potion).update();
                        logMetric(ID, "Potion", null, null, null, null, Collections.singletonList(potion.ID), null, null, 0, 0, 0, 0, 0, this.potionCost);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Heal
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(this.healCost);
                        AbstractDungeon.player.heal(this.healAmount);
                        logMetricHealAtCost(ID, "Heal", this.healCost, this.healAmount);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Spells
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(this.spellsCost);
                        this.showCardReward(SPELLS);
                        this.logMetricLoseGold(ID, "Spells", this.spellsCost);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
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

    private void showCardReward(int numCards) {
        AbstractDungeon.getCurrRoom().rewards.clear();
        for(int i = 0; i < numCards; ++i) {
            RewardItem reward = new RewardItem();
            reward.cards = CardUtil.getGrandMagusSpellReward();
            AbstractDungeon.getCurrRoom().addCardReward(reward);
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
}
