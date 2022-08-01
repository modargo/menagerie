package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;
import menagerie.cards.BeastSpirit;

import java.text.MessageFormat;
import java.util.Collections;

public class BeastSpiritShrine extends AbstractImageEvent {
    public static final String ID = "Menagerie:BeastSpiritShrine";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int MAX_HEALTH_LOSS = 3;
    private static final int A15_MAX_HEALTH_LOSS = 5;

    private final int maxHealthLoss;
    private final AbstractCard card;
    private final boolean hasPrismaticShard;
    private int screenNum = 0;

    public BeastSpiritShrine() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.card = new BeastSpirit();
        this.hasPrismaticShard = AbstractDungeon.player.hasRelic(PrismaticShard.ID);
        this.maxHealthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS : MAX_HEALTH_LOSS;

        if (!this.hasPrismaticShard) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.maxHealthLoss), this.card);
        }
        else {
            imageEventText.setDialogOption(OPTIONS[1], this.card);
        }
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Kneel
                        if (!this.hasPrismaticShard) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                            AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                            logMetricObtainCardsLoseMapHP(ID, "Knelt", Collections.singletonList(this.card.cardID), this.maxHealthLoss);
                        }
                        else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            logMetricObtainCard(ID, "Presented Prismatic Shard", this.card);
                        }
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

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
}
