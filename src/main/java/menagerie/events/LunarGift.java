package menagerie.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import menagerie.Menagerie;
import menagerie.relics.ShiftingBlessing;

import java.text.MessageFormat;

public class LunarGift extends AbstractImageEvent {
    public static final String ID = "Menagerie:LunarGift";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final int HEALTH_LOSS = 9;
    private static final int A15_HEALTH_LOSS = 12;

    private int healthLoss;
    private AbstractRelic relic;
    private int screenNum = 0;

    public LunarGift() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new ShiftingBlessing();
        this.healthLoss = AbstractDungeon.ascensionLevel >= 15 ? A15_HEALTH_LOSS : HEALTH_LOSS;

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.healthLoss), this.relic);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Kneel
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.healthLoss));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.LIGHTNING));
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relic);
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
