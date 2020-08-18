package menagerie.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.purple.Strike_Purple;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import menagerie.Menagerie;

import java.text.MessageFormat;
import java.util.Arrays;

public class ShimmeringGrove extends AbstractImageEvent {
    public static final String ID = "Menagerie:ShimmeringGrove";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final float MAX_HEALTH_LOSS_PERCENT = 0.03F;
    private static final int A15_MAX_HEALTH_LOSS_ADDITION = 1;

    private int maxHealthLoss;
    private AbstractCard card;
    private AbstractRelic relic;
    private int screenNum = 0;

    public ShimmeringGrove() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.card = this.getCard();
        this.relic = new PrismaticShard();
        this.maxHealthLoss = (int)((float)AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENT) + (AbstractDungeon.ascensionLevel >= 15 ? A15_MAX_HEALTH_LOSS_ADDITION : 0);

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.relic.name, this.maxHealthLoss), this.relic);
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.card.name), this.card);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: //
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);

                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: //
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);

                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            case 1:
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);

                AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), this.relic);
                AbstractDungeon.shopRelicPool.remove(this.relic.relicId);
                this.screenNum = 2;
                this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                this.imageEventText.clearRemainingOptions();
                break;
            default:
                this.openMap();
                break;
        }
    }

    public AbstractCard getCard() {
        AbstractCard[] cards = new AbstractCard[] {
                new Strike_Red(),
                new Strike_Green(),
                new Strike_Blue(),
                new Strike_Purple()
        };

        cards = Arrays.stream(cards).filter(c -> c.color != AbstractDungeon.player.getCardColor()).toArray(AbstractCard[]::new);

        return cards[AbstractDungeon.eventRng.random(cards.length - 1)];
    }
}
