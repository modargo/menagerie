package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.cards.CardUtil;
import menagerie.relics.MalignantTentacle;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CorruptingLight extends AbstractImageEvent {
    public static final String ID = "Menagerie:CorruptingLight";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static int UPGRADES = 2;

    private AbstractRelic relic;
    private int screenNum = 0;

    public CorruptingLight() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new MalignantTentacle();

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], UPGRADES, this.relic.name), this.relic);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Enter
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), this.relic);
                        AbstractCard card1 = CardUtil.upgradeRandomCard();
                        AbstractCard card2 = CardUtil.upgradeRandomCard();
                        List<String> cards = new ArrayList<>();
                        if (card1 != null) {
                            cards.add(card1.cardID);
                        }
                        if (card2 != null) {
                            cards.add(card2.cardID);
                        }
                        logMetric(ID, "Enter", null, null, null, cards, Collections.singletonList(this.relic.relicId), null, null, 0, 0, 0, 0, 0, 0);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        logMetricIgnored(ID);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
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
