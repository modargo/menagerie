package menagerie.events;

import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;

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
    private static final int UPGRADE_COST = 85;
    private static final int A15_UPGRADE_COST = 95;
    private static final float HEAL_PERCENT = 0.25f;

    private int potionCost;
    private int healCost;
    private int upgradeCost;
    private int healAmount;
    private int screenNum = 0;

    public MagicPeddler() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.potionCost = AbstractDungeon.ascensionLevel >= 15 ? A15_POTION_COST : POTION_COST;
        this.healCost = AbstractDungeon.ascensionLevel >= 15 ? A15_HEAL_COST : HEAL_COST;
        this.upgradeCost = AbstractDungeon.ascensionLevel >= 15 ? A15_UPGRADE_COST : UPGRADE_COST;
        this.healAmount = (int)(AbstractDungeon.player.maxHealth * HEAL_PERCENT);

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.potionCost));
        if (AbstractDungeon.player.gold >= this.healCost) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healCost, this.healAmount));
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.healCost), true);
        }
        if (AbstractDungeon.player.gold >= this.upgradeCost) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.upgradeCost));
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.upgradeCost), true);
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
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Heal
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(this.healCost);
                        AbstractDungeon.player.heal(this.healAmount);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Upgrade
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(this.upgradeCost);
                        CardUtil.upgradeRandomCard();
                        CardUtil.upgradeRandomCard();
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.openMap();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }


}
