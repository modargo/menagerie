package menagerie.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import menagerie.cards.spells.*;
import menagerie.util.CollectionsUtil;

import java.util.*;

public class CardUtil {
    public static AbstractCard upgradeRandomCard() {
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

    public static int getNumCardsForRewards() {
        int numCards = 3;
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            numCards = r.changeNumberOfCardsInReward(numCards);
        }
        if (ModHelper.isModEnabled("Binary")) {
            --numCards;
        }
        return numCards;
    }

    public static AbstractCard getOtherColorCard(AbstractCard.CardRarity rarity, List<AbstractCard.CardColor> excludedColors) {
        CardGroup anyCard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        Iterator<Map.Entry<String, AbstractCard>> var2 = CardLibrary.cards.entrySet().iterator();

        while(true) {
            Map.Entry<String, AbstractCard> c;
            do {
                do {
                    do {
                        do {
                            if (!var2.hasNext()) {
                                anyCard.shuffle(AbstractDungeon.cardRng);
                                return anyCard.getRandomCard(true, rarity).makeCopy();
                            }

                            c = var2.next();
                        } while(c.getValue().rarity != rarity || excludedColors.contains(c.getValue().color));
                    } while(c.getValue().type == AbstractCard.CardType.CURSE);
                } while(c.getValue().type == AbstractCard.CardType.STATUS);
            } while(UnlockTracker.isCardLocked(c.getKey()) && !Settings.treatEverythingAsUnlocked());

            anyCard.addToBottom(c.getValue());
        }
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
        int numCards = CardUtil.getNumCardsForRewards();
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
        //Removing these spells from being generated in favor of new additions, but leaving them in the code so that
        //existing and historical runs don't break
        list.removeIf(c -> c.cardID.equals(RelicOfProgenitus.ID));
        list.removeIf(c -> c.cardID.equals(Skullclamp.ID));
        CollectionsUtil.shuffle(list, random);
        return new ArrayList<>(list);
    }

    public static ArrayList<AbstractCard> getAllGrandMagusSpells() {
        List<AbstractCard> list = Arrays.asList(
                new CrumblingSanctuary(),
                new DarkRitual(),
                new Dismember(),
                new Foresee(),
                new Hexdrinker(),
                new Languish(),
                new LightningBolt(),
                new LightningHelix(),
                new LoxodonWarhammer(),
                new MirarisWake(),
                new QuirionDryad(),
                new RelicOfProgenitus(),
                new Skullclamp(),
                new Staggershock(),
                new SteelWall(),
                new SuddenReclamation(),
                new Tinker(),
                new WallOfBlossoms()
        );
        return new ArrayList<>(list);
    }
}
