package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;
import menagerie.cards.BeastSpirit;

import java.text.MessageFormat;

public class BeastSpiritShrine extends AbstractImageEvent {
    public static final String ID = "Menagerie:BeastSpiritShrine";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int MAX_HEALTH_LOSS = 5;
    private static final int A15_MAX_HEALTH_LOSS = 3;

    private int maxHealthLoss;
    private AbstractCard card;
    private int screenNum = 0;

    public BeastSpiritShrine() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.card = new BeastSpirit();
        this.maxHealthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS : MAX_HEALTH_LOSS;

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.maxHealthLoss), this.card);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Kneel
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
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
