package menagerie.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SuddenReclamationMillAction extends AbstractGameAction {
    private float startingDuration;

    public SuddenReclamationMillAction(int amount) {
        this.target = AbstractDungeon.player;
        this.actionType = ActionType.WAIT;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = amount;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            CardGroup deck = AbstractDungeon.player.drawPile;
            int mill = Math.min(this.amount, deck.size());
            for (int i=0; i < mill; i++) {
                deck.moveToDiscardPile(deck.getTopCard());
            }

            this.isDone = true;
        }
    }
}
