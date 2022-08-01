package menagerie.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import menagerie.Menagerie;
import menagerie.monsters.specials.GrandMagus;
import menagerie.relics.GrandMagusSpellbook;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class GrandMagusTower extends Colosseum {
    public static final String ID = "Menagerie:GrandMagusTower";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final String ENCOUNTER = GrandMagus.ID;

    private final AbstractRelic relic;
    private int screenNum = 0;

    public GrandMagusTower() {
        super();
        this.imageEventText.clear();
        this.roomEventText.clear();
        this.title = NAME;
        this.imageEventText.loadImage(IMG);
        type = EventType.IMAGE;
        this.noCardsInRewards = true;

        this.relic = new GrandMagusSpellbook();

        this.body = DESCRIPTIONS[0];
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screenNum = 1;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[1], this.relic);
                this.imageEventText.setDialogOption(OPTIONS[2]);
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        if (!AbstractDungeon.player.hasRelic(this.relic.relicId)) {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relic);
                        }
                        else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new Circlet());
                        }
                        logMetricObtainRelic(ID, "Steal", this.relic);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 2;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        break;
                    default:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        logMetricIgnored(ID);
                        this.screenNum = 3;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                }
                break;
            case 2:
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(ENCOUNTER);
                AbstractDungeon.getCurrRoom().rewards.clear();
                AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                this.enterCombatFromImage();
                AbstractDungeon.lastCombatMetricKey = ENCOUNTER;
                break;
            default:
                this.openMap();
                break;
        }
    }

    @Override
    public void reopen() {
    }
}
