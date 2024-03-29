package menagerie.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import menagerie.Menagerie;
import menagerie.relics.GhostlyEgg;

import java.text.MessageFormat;

public class Silence extends AbstractImageEvent {
    public static final String ID = "Menagerie:Silence";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int HEALTH_LOSS = 9;
    private static final int A15_HEALTH_LOSS = 12;

    private final int healthLoss;
    private final AbstractRelic relic;
    private int screenNum = 0;

    public Silence() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new GhostlyEgg();
        this.healthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_HEALTH_LOSS : HEALTH_LOSS;

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.relic.name, this.healthLoss), this.relic);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Explore
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.SLASH_HEAVY));
                        if (!AbstractDungeon.player.hasRelic(this.relic.relicId)) {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), this.relic);
                        }
                        else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new Circlet());
                        }
                        logMetricObtainRelicAndDamage(ID, "Explore", this.relic, this.healthLoss);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        logMetricIgnored(ID);
                        this.screenNum = 1;
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
