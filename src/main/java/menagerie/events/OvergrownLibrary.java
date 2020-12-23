package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;

import java.text.MessageFormat;
import java.util.Collections;

public class OvergrownLibrary extends AbstractImageEvent {
    public static final String ID = "Menagerie:OvergrownLibrary";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final float MAX_HEALTH_LOSS_PERCENT = 0.10F;
    private static final float A15_MAX_HEALTH_LOSS_PERCENT = 0.13F;
    private static final int MAX_HEALTH_LOSS_FLOOR = 3;
    private static final int A15_MAX_HEALTH_LOSS_FLOOR = 4;

    private int maxHealthLoss;
    private AbstractCard curse;
    private int screenNum = 0;

    public OvergrownLibrary() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;

        this.curse = new Doubt();
        float maxHealthLossPercent = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS_PERCENT : MAX_HEALTH_LOSS_PERCENT;
        int maxHealthLossFloor = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS_FLOOR : MAX_HEALTH_LOSS_FLOOR;
        this.maxHealthLoss = Math.max((int)((float)AbstractDungeon.player.maxHealth * maxHealthLossPercent), maxHealthLossFloor);

        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.maxHealthLoss), this.curse);
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Peek
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        logMetric(ID, "Peek");
                        this.showCardReward(1);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Read
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        logMetricObtainCardsLoseMapHP(ID, "Read", Collections.singletonList(this.curse.cardID), this.maxHealthLoss);
                        this.showCardReward(3);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        logMetricIgnored(ID);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        this.openMap();
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
            reward.cards = CardUtil.getGrandMagusSpellReward();
            AbstractDungeon.getCurrRoom().addCardReward(reward);
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
}
