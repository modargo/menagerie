package menagerie.act;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.scenes.TheBottomScene;
import menagerie.monsters.elites.Hydra;
import menagerie.monsters.elites.MaskedSummoner;
import menagerie.monsters.normals.*;
import menagerie.monsters.elites.VoidReaper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MenagerieAct extends CustomDungeon {
    public static final String ID = "Menagerie:Menagerie";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    public static final int ACT_NUM = 1;
    private static final Logger logger = LogManager.getLogger(MenagerieAct.class.getName());

    public MenagerieAct() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        logger.info("MenagerieAct constructor");
    }

    public MenagerieAct(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }
    public MenagerieAct(CustomDungeon cd, AbstractPlayer p, SaveFile sf) {
        super(cd, p, sf);
    }

    @Override
    public AbstractScene DungeonScene() {
        logger.info("MenagerieAct DungeonScene");
        return new TheBottomScene();
    }

    @Override
    public String getBodyText() {
        return TEXT[2];
    }

    @Override
    public String getOptionText() {
        return TEXT[3];
    }

    @Override
    protected void initializeLevelSpecificChances() {
        //These are all deliberately the same as Exordium
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        cardUpgradedChance = 0.0F;
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(3);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(MonstrousExperiment.ID, 25.0F));
        monsters.add(new MonsterInfo(WhisperingWraith.ID, 25.0F));
        monsters.add(new MonsterInfo(StygianBoar.ID, 25.0F));
        monsters.add(new MonsterInfo(Encounters.RABBITS_2, 25.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(EntropyWarlock.ID, 16.0F));
        monsters.add(new MonsterInfo(MeltingSalamander.ID, 16.0F));
        monsters.add(new MonsterInfo(RedMage.ID, 16.0F));
        monsters.add(new MonsterInfo(Hexasnake.ID, 16.0F));
        monsters.add(new MonsterInfo(Encounters.BEAST_MAGE_AND_PROWLING_AMALGAM, 16.0F));
        monsters.add(new MonsterInfo(Encounters.KEEPER_AND_YOUNG_SUNSTALKER, 16.0F));
        monsters.add(new MonsterInfo(Encounters.MONSTROUS_EXPERIMENTS_2, 16.0F));
        monsters.add(new MonsterInfo(Encounters.RABBITS_3, 16.0F));
        monsters.add(new MonsterInfo(Encounters.STYGIAN_BOAR_AND_WHISPERING_WRAITH, 16.0F));
        monsters.add(new MonsterInfo(Encounters.STYGIAN_BOAR_AND_MONSTROUS_EXPERIMENT, 16.0F));
        monsters.add(new MonsterInfo(Encounters.DREAD_MOTHS_AND_GRAFTED_WORM, 16.0F));
        monsters.add(new MonsterInfo(Encounters.MENAGERIE_WILDLIFE, 16.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(Hydra.ID, 1.0F));
        monsters.add(new MonsterInfo(VoidReaper.ID, 1.0F));
        monsters.add(new MonsterInfo(MaskedSummoner.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch (monsterList.get(monsterList.size() - 1))
        {
            case MonstrousExperiment.ID:
                retVal.add(Encounters.MONSTROUS_EXPERIMENTS_2);
                retVal.add(Encounters.STYGIAN_BOAR_AND_MONSTROUS_EXPERIMENT);
                break;
            case WhisperingWraith.ID:
                retVal.add(Encounters.STYGIAN_BOAR_AND_WHISPERING_WRAITH);
                break;
            case StygianBoar.ID:
                retVal.add(Encounters.STYGIAN_BOAR_AND_MONSTROUS_EXPERIMENT);
                retVal.add(Encounters.STYGIAN_BOAR_AND_WHISPERING_WRAITH);
        }

        return retVal;
    }

    @Override
    protected void initializeShrineList() {
        // No shrines in this act, since we want to experience as many of the new events as possible
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in Menagerie.addEvents()
    }
}
