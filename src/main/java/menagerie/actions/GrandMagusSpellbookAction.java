package menagerie.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import menagerie.cards.CardUtil;

import java.util.ArrayList;

public class GrandMagusSpellbookAction extends AbstractGameAction {
    private boolean retrieveCard = false;
    private static final int CARDS = 3;

    public GrandMagusSpellbookAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.isDone = true;
        } else if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.generateCardChoices(), CardRewardScreen.TEXT[1], true);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard spell = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    spell.current_x = -1000.0F * Settings.scale;
                    AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(spell, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }
                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        return CardUtil.getGrandMagusSpells(CARDS, AbstractDungeon.cardRandomRng);
    }
}
