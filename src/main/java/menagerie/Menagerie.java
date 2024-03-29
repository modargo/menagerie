package menagerie;

import actlikeit.dungeons.CustomDungeon;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.exordium.GoldenIdolEvent;
import com.megacrit.cardcrawl.events.shrines.AccursedBlacksmith;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import menagerie.act.Encounters;
import menagerie.act.MenagerieAct;
import menagerie.cards.*;
import menagerie.events.*;
import menagerie.monsters.bosses.*;
import menagerie.monsters.elites.Hydra;
import menagerie.monsters.elites.MaskedSummoner;
import menagerie.monsters.elites.VoidReaper;
import menagerie.monsters.normals.*;
import menagerie.monsters.specials.GrandMagus;
import menagerie.relics.*;
import menagerie.util.TextureLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

import static com.megacrit.cardcrawl.core.Settings.GameLanguage;
import static com.megacrit.cardcrawl.core.Settings.language;

@SpireInitializer
public class Menagerie implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber {
    private static final float X1 = -350.0F;
    private static final float X2 = 0.0F;
    private static final float X2_ALT = 100.0F;

    public static final Logger logger = LogManager.getLogger(Menagerie.class.getName());

    public Menagerie() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new Menagerie();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("menagerie/images/MenagerieBadge.png");
        BaseMod.registerModBadge(badgeTexture, "Menagerie", "modargo", "An alternate act 1 themed around beasts and mages. Once the stronghold of a cabal of mages who experimented with the boundaries of life itself, the Menagerie is now overrun by their twisted creations.", new ModPanel());

        CustomDungeon.addAct(MenagerieAct.ACT_NUM, new MenagerieAct());
        addActAndMonsters();
        addEvents();
    }

    private static void addActAndMonsters() {
        CustomDungeon act = new MenagerieAct();
        act.addAct(MenagerieAct.ACT_NUM);

        //Weak encounters
        BaseMod.addMonster(MonstrousExperiment.ID, (BaseMod.GetMonster)MonstrousExperiment::new);
        BaseMod.addMonster(WhisperingWraith.ID, (BaseMod.GetMonster)WhisperingWraith::new);
        BaseMod.addMonster(StygianBoar.ID, (BaseMod.GetMonster)StygianBoar::new);
        BaseMod.addMonster(Encounters.RABBITS_2, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new SilkyRabbit(X1, 0.0F),
                        new FeatherRabbit(X2, 0.0F)
                }));

        //Hard encounters
        BaseMod.addMonster(EntropyWarlock.ID, (BaseMod.GetMonster)EntropyWarlock::new);
        BaseMod.addMonster(MeltingSalamander.ID, (BaseMod.GetMonster)MeltingSalamander::new);
        BaseMod.addMonster(RedMage.ID, (BaseMod.GetMonster)RedMage::new);
        BaseMod.addMonster(Hexasnake.ID, (BaseMod.GetMonster)Hexasnake::new);
        BaseMod.addMonster(Encounters.BEAST_MAGE_AND_PROWLING_AMALGAM, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new ProwlingAmalgam(X1, 0.0F),
                        new BeastMage(X2_ALT, 0.0F)
                }));
        BaseMod.addMonster(Encounters.KEEPER_AND_YOUNG_SUNEATER, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new YoungSuneater(X1, 0.0F),
                        new KeeperOfTheSacredPool(X2_ALT, 0.0F)
                }));
        BaseMod.addMonster(Encounters.STYGIAN_BOAR_AND_WHISPERING_WRAITH, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new WhisperingWraith(X1, 0.0F),
                        new StygianBoar(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.STYGIAN_BOAR_AND_RABBIT, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new SilkyRabbit(X1, 0.0F),
                        new StygianBoar(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.RABBITS_3, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new SilkyRabbit(-400.0F, 0.0F),
                        new FeatherRabbit(-175.0F, 0.0F, FeatherRabbit.FirstMove.ATTACK),
                        new FeatherRabbit(50.0F, 0.0F, FeatherRabbit.FirstMove.BUFF)
                }));
        BaseMod.addMonster(Encounters.DREAD_MOTH_AND_GRAFTED_WORMS, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new GraftedWorm(-550.0F, 0.0F),
                        new GraftedWorm(-300.0F, 0.0F),
                        new GraftedWorm(-50.0F, 0.0F),
                        new DreadMoth(200.0F, 125.0F),
                }));
        BaseMod.addMonster(Encounters.MENAGERIE_WILDLIFE, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new GraftedWorm(-500.0F, 0.0F),
                        new DreadMoth(-150.0F, 125.0F),
                        new FeatherRabbit(200.0F, 0.0F)
                }));

        //Elites
        BaseMod.addMonster(Hydra.ID, (BaseMod.GetMonster)Hydra::new);
        BaseMod.addMonster(VoidReaper.ID, (BaseMod.GetMonster)VoidReaper::new);
        BaseMod.addMonster(MaskedSummoner.ID, () -> new MaskedSummoner(200.0F, 0.0F));

        //Bosses
        act.addBoss(Chimera.ID, (BaseMod.GetMonster)Chimera::new,"menagerie/images/map/bosses/Chimera.png", "menagerie/images/map/bosses/ChimeraOutline.png");
        act.addBoss(Suneater.ID, (BaseMod.GetMonster) Suneater::new, "menagerie/images/map/bosses/Suneater.png", "menagerie/images/map/bosses/SuneaterOutline.png");
        act.addBoss(Encounters.AVATARS, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new AvatarOfVigor(-550.0F, 50.0F),
                        new AvatarOfCunning(-200.0F, 50.0F),
                        new AvatarOfWisdom(150.0F, 0.0F),
                }), "menagerie/images/map/bosses/Avatars.png", "menagerie/images/map/bosses/AvatarsOutline.png");

        //Special fights
        BaseMod.addMonster(GrandMagus.ID, (BaseMod.GetMonster)GrandMagus::new);
        BaseMod.addMonster(Encounters.MOSSY_WILDLIFE, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new DreadMoth(-500.0F, 125.0F),
                        new SilkyRabbit(-250.0F, 0.0F),
                        new MonstrousExperiment(0.0F, 0.0F)
                }));
    }

    private static void addEvents() {
        // Golden Idol has enough hooks for future events (including in the Elementarium) that we want to keep it around
        BaseMod.addEvent(GoldenIdolEvent.ID, GoldenIdolEvent.class, MenagerieAct.ID);
        // Accursed Blacksmith is a cool enough event that we want to keep it around
        BaseMod.addEvent(AccursedBlacksmith.ID, AccursedBlacksmith.class, MenagerieAct.ID);

        BaseMod.addEvent(BeastSpiritShrine.ID, BeastSpiritShrine.class, MenagerieAct.ID);
        BaseMod.addEvent(MageAndMonster.ID, MageAndMonster.class, MenagerieAct.ID);
        BaseMod.addEvent(OvergrownLibrary.ID, OvergrownLibrary.class, MenagerieAct.ID);
        BaseMod.addEvent(ThreeAnimals.ID, ThreeAnimals.class, MenagerieAct.ID);
        BaseMod.addEvent(BigGameHunter.ID, BigGameHunter.class, MenagerieAct.ID);
        BaseMod.addEvent(ScentOfGold.ID, ScentOfGold.class, MenagerieAct.ID);
        BaseMod.addEvent(StasisChamber.ID, StasisChamber.class, MenagerieAct.ID);
        BaseMod.addEvent(SpiderNest.ID, SpiderNest.class, MenagerieAct.ID);
        BaseMod.addEvent(MossyPath.ID, MossyPath.class, MenagerieAct.ID);
        BaseMod.addEvent(LunarGift.ID, LunarGift.class, MenagerieAct.ID);
        BaseMod.addEvent(MagicPeddler.ID, MagicPeddler.class, MenagerieAct.ID);
        BaseMod.addEvent(HarrowingRitual.ID, HarrowingRitual.class, MenagerieAct.ID);
        BaseMod.addEvent(MagesHand.ID, MagesHand.class, MenagerieAct.ID);
        BaseMod.addEvent(CloudVision.ID, CloudVision.class, MenagerieAct.ID);
        BaseMod.addEvent(GrandMagusTower.ID, GrandMagusTower.class, MenagerieAct.ID);
        BaseMod.addEvent(CorruptingLight.ID, CorruptingLight.class, MenagerieAct.ID);
        BaseMod.addEvent(ShimmeringGrove.ID, ShimmeringGrove.class, MenagerieAct.ID);
        BaseMod.addEvent(Silence.ID, Silence.class, MenagerieAct.ID);
        BaseMod.addEvent(AtThePeak.ID, AtThePeak.class, MenagerieAct.ID);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Necropotence());
        BaseMod.addCard(new PurgingElixir());
        BaseMod.addCard(new BeastSpirit());
        BaseMod.addCard(new Slaughter());
        BaseMod.addCard(new SagesJudgement());
        BaseMod.addCard(new BloodySpellbook());

        //Grand Magus Spells
        for (AbstractCard c : CardUtil.getAllGrandMagusSpells()) {
            BaseMod.addCard(c);
        }
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Aurumvore(), RelicType.SHARED);
        BaseMod.addRelic(new GrandMagusSpellbook(), RelicType.SHARED);
        BaseMod.addRelic(new HuntersKnife(), RelicType.SHARED);
        BaseMod.addRelic(new ShiftingBlessing(), RelicType.SHARED);
        BaseMod.addRelic(new MalignantTentacle(), RelicType.SHARED);
        BaseMod.addRelic(new GhostlyEgg(), RelicType.SHARED);
        BaseMod.addRelic(new ExplorersFlag(), RelicType.SHARED);
    }

    private static String makeLocPath(Settings.GameLanguage language, String filename)
    {
        String ret = "localization/";
        switch (language) {
            case KOR:
                ret += "kor";
                break;
            case ZHS:
                ret += "zhs";
                break;
            default:
                ret += "eng";
                break;
        }
        return "menagerie/" + ret + "/" + filename + ".json";
    }

    private void loadLocFiles(GameLanguage language)
    {
        BaseMod.loadCustomStringsFile(CardStrings.class, makeLocPath(language, "Menagerie-Card-Strings"));
        BaseMod.loadCustomStringsFile(EventStrings.class, makeLocPath(language, "Menagerie-Event-Strings"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class, makeLocPath(language, "Menagerie-Monster-Strings"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makeLocPath(language, "Menagerie-Relic-Strings"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makeLocPath(language, "Menagerie-Power-Strings"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "Menagerie-ui"));
    }

    @Override
    public void receiveEditStrings()
    {
        loadLocFiles(GameLanguage.ENG);
        if (language != GameLanguage.ENG) {
            loadLocFiles(language);
        }
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(makeLocPath(Settings.language, "Menagerie-Keyword-Strings")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                //The modID here must be lowercase
                BaseMod.addKeyword("menagerie", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    public static String cardImage(String id) {
        return "menagerie/images/cards/" + removeModId(id) + ".png";
    }
    public static String eventImage(String id) {
        return "menagerie/images/events/" + removeModId(id) + ".png";
    }
    public static String relicImage(String id) {
        return "menagerie/images/relics/" + removeModId(id) + ".png";
    }
    public static String powerImage32(String id) {
        return "menagerie/images/powers/" + removeModId(id) + "32.png";
    }
    public static String powerImage84(String id) {
        return "menagerie/images/powers/" + removeModId(id) + "84.png";
    }
    public static String monsterImage(String id) {
        return "menagerie/images/monsters/" + removeModId(id) + "/" + removeModId(id) + ".png";
    }
    public static String relicOutlineImage(String id) {
        return "menagerie/images/relics/outline/" + removeModId(id) + ".png";
    }

    public static String removeModId(String id) {
        if (id.startsWith("Menagerie:")) {
            return id.substring(id.indexOf(':') + 1);
        } else {
            logger.warn("Missing mod id on: " + id);
            return id;
        }
    }

    public static void LoadPowerImage(AbstractPower power) {
        Texture tex84 = TextureLoader.getTexture(Menagerie.powerImage84(power.ID));
        Texture tex32 = TextureLoader.getTexture(Menagerie.powerImage32(power.ID));
        power.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        power.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    private static class Keyword
    {
        public String PROPER_NAME;
        public String[] NAMES;
        public String DESCRIPTION;
    }
}