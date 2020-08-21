package menagerie.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import menagerie.cards.spells.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardUtil {
    public static AbstractCard upgradeRandomCard() {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }

        if (!upgradableCards.isEmpty()) {
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractCard card = upgradableCards.get(AbstractDungeon.miscRng.random(upgradableCards.size() - 1));
            card.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(card);
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            return card;
        }
        return null;
    }

    public static AbstractCard getGrandMagusSpell() {
        ArrayList<AbstractCard> list = CardUtil.getRandomlyOrderedGrandMagusSpells(AbstractDungeon.cardRng);
        return list.get(0);
    }

    public static ArrayList<AbstractCard> getGrandMagusSpells(int count, Random random) {
        ArrayList<AbstractCard> list = CardUtil.getRandomlyOrderedGrandMagusSpells(random);
        ArrayList<AbstractCard> list2 = new ArrayList<>();
        for (AbstractCard c : list) {
            if (!c.hasTag(AbstractCard.CardTags.HEALING)) {
                list2.add(c);
            }
        }
        return new ArrayList<>(list2.subList(0, count));
    }

    public static ArrayList<AbstractCard> getGrandMagusSpellReward() {
        ArrayList<AbstractCard> list = CardUtil.getRandomlyOrderedGrandMagusSpells(AbstractDungeon.cardRng);
        int numCards = 3;
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            numCards = r.changeNumberOfCardsInReward(numCards);
        }
        if (ModHelper.isModEnabled("Binary")) {
            --numCards;
        }
        List<AbstractCard> rewardCards = list.subList(0, numCards);
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            for (AbstractCard c : rewardCards) {
                r.onPreviewObtainCard(c);
            }
        }

        return new ArrayList<>(rewardCards);
    }

    private static ArrayList<AbstractCard> getRandomlyOrderedGrandMagusSpells(Random random) {
        ArrayList<AbstractCard> list = CardUtil.getAllGrandMagusSpells();
        Collections.shuffle(list, random.random);
        return new ArrayList<>(list);
    }

    public static ArrayList<AbstractCard> getAllGrandMagusSpells() {
        List<AbstractCard> list = Arrays.asList(
                new CrumblingSanctuary(),
                new DarkRitual(),
                new Dismember(),
                new Foresee(),
                new Languish(),
                new LightningBolt(),
                new LightningHelix(),
                new LoxodonWarhammer(),
                new MirarisWake(),
                new RelicOfProgenitus(),
                new Skullclamp(),
                new Staggershock(),
                new SteelWall(),
                new Tinker(),
                new WallOfBlossoms()
        );
        return new ArrayList<>(list);
    }
}
