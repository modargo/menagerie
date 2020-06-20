package menagerie.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import menagerie.Menagerie;
import menagerie.cards.BeastSpirit;
import menagerie.relics.Aurumvore;

import java.text.MessageFormat;

public class ScentOfGold extends AbstractImageEvent {
    public static final String ID = "Menagerie:ScentOfGold";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int HEALTH_LOSS_PERCENT = 20;
    private static final int A15_HEALTH_LOSS_PERCENT = 30;

    private int healthLoss;
    private AbstractRelic relic;
    private int screenNum = 0;

    public ScentOfGold() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new Aurumvore();
        float healthLossPercent = AbstractDungeon.ascensionLevel >= 15 ? A15_HEALTH_LOSS_PERCENT : HEALTH_LOSS_PERCENT;
        this.healthLoss = (int)((float)AbstractDungeon.player.maxHealth * healthLossPercent);

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.relic.name), this.relic);
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.healthLoss));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Pacify
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relic);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // LeaRunve
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.SLASH_HEAVY));
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
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
