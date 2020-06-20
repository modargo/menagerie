package menagerie.events;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import menagerie.Menagerie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class StasisChamber extends Colosseum {
    public static final String ID = "Menagerie:StasisChamber";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Menagerie.eventImage(ID);
    private static final Logger logger = LogManager.getLogger(StasisChamber.class.getName());

    private static final String GensokyoActId = "Gensokyo:Gensokyo";
    private static final String HallownestActId = "Hallownest:Greenpath";

    private int screenNum = 0;
    private String actID;
    private int maxHpLoss;

    public StasisChamber() {
        super();
        this.imageEventText.clear();
        this.roomEventText.clear();
        this.title = NAME;
        this.imageEventText.loadImage(IMG);
        this.type = EventType.IMAGE;
        this.noCardsInRewards = false;
        this.actID = this.getRandomAct();

        this.body = DESCRIPTIONS[0].replace("{0}", this.getNameForAct(this.actID));
        this.imageEventText.setDialogOption(OPTIONS[0].replace("{0}", this.getNameForAct(this.actID)));
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    private String getRandomAct() {
        List<String> acts = new ArrayList<>();
        if (Loader.isModLoaded("Gensokyo")) {
            acts.add(GensokyoActId);
        }
        if (Loader.isModLoaded("Hallownest")) {
            acts.add(HallownestActId);
        }
        // We always prioritize other modded acts over the base game, since crossover content is fun
        if (acts.isEmpty()){
            acts.add(Exordium.ID);
        }

        String act = acts.get(AbstractDungeon.miscRng.random(acts.size() - 1));
        logger.info("Determined random act to pull elite from: " + act);
        return act;
    }

    private String getRandomElite(String actID){
        List<String> elites = new ArrayList<>();
        // Rather than try to do something fancy to automatically pull elites, we'll just hardcode support for specific
        // alternate acts. There aren't many and it's easy to update.
        switch (actID) {
            case GensokyoActId:
                elites.add("Gensokyo:Mamizou");
                elites.add("Gensokyo:Aya");
                elites.add("Gensokyo:Cirno");
                break;
            case HallownestActId:
                elites.add("Hallownest:eliteMossKnight");
                elites.add("Hallownest:eliteCrystalGuardian");
                elites.add("Hallownest:eliteVengeflyKing");
                break;
            default:
                if (actID != Exordium.ID) {
                    logger.warn("Unknown act for getting random elite: " + actID);
                }
                elites.add("Gremlin Nob");
                elites.add("3 Sentries");
                elites.add("Lagavulin");
        }
        return elites.get(AbstractDungeon.miscRng.random(elites.size() - 1));
    }

    private String getNameForAct(String actID) {
        logger.info("Getting act name. Acts available: " + String.join(", ", CustomDungeon.dungeons.keySet()));
        if (CustomDungeon.dungeons.containsKey(actID)) {
            return CustomDungeon.dungeons.get(actID).name;
        }
        return Exordium.NAME;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Release
                        this.screenNum = 2;
                        String elite = this.getRandomElite(this.actID);
                        logger.info("Spawning elite: " + elite);
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(elite);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = elite;
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

    @Override
    public void reopen() {
    }
}
