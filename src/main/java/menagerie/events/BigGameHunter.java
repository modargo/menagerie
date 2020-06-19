package menagerie.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;
import menagerie.cards.BeastSpirit;
import menagerie.relics.HuntersKnife;

import java.text.MessageFormat;

public class BigGameHunter extends AbstractImageEvent {
    public static final String ID = "Menagerie:BigGameHunter";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final float MAX_HEALTH_LOSS_PERCENT = 0.08f;
    private static final float A15_MAX_HEALTH_LOSS_PERCENT = 0.1f;

    private int maxHealthLoss;
    private AbstractRelic relic;
    private int screenNum = 0;

    public BigGameHunter() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new HuntersKnife();
        float maxHealthLossPercent = AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS_PERCENT : MAX_HEALTH_LOSS_PERCENT;
        this.maxHealthLoss = (int)((float)AbstractDungeon.player.maxHealth * maxHealthLossPercent);

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screenNum = 1;
                this.imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.maxHealthLoss), this.relic);
                imageEventText.setDialogOption(OPTIONS[2]);
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: //Take
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 2;
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relic);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
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
