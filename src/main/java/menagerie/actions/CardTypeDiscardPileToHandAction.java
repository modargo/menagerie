package menagerie.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CardTypeDiscardPileToHandAction extends AbstractGameAction {
    public static final Logger logger = LogManager.getLogger(CardTypeDiscardPileToHandAction.class.getName());
    public static final String[] TEXT;
    private AbstractPlayer player;
    private int numberOfCards;
    private boolean optional;
    private int newCost;
    private boolean setCost;
    private AbstractCard.CardType cardType;

    public CardTypeDiscardPileToHandAction(int numberOfCards, boolean optional, AbstractCard.CardType cardType) {
        this.newCost = 0;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.player = AbstractDungeon.player;
        this.numberOfCards = numberOfCards;
        this.optional = optional;
        this.setCost = false;
        this.cardType = cardType;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            CardGroup eligibleCards = this.getEligibleCards();
            logger.info("Returning " + this.numberOfCards + " card(s). Eligible cards: " + eligibleCards.group.stream().map(c -> c.cardID).collect(Collectors.joining(", ")));
            if (!eligibleCards.isEmpty() && this.numberOfCards > 0) {
                if (eligibleCards.size() <= this.numberOfCards && !this.optional) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(eligibleCards.group);

                    for (AbstractCard c : cardsToMove) {
                        if (this.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                            this.player.hand.addToHand(c);
                            if (this.setCost) {
                                c.setCostForTurn(this.newCost);
                            }

                            this.player.discardPile.removeCard(c);
                        }

                        c.lighten(false);
                        c.applyPowers();
                    }

                    this.isDone = true;
                } else {
                    String text = this.getText(numberOfCards);
                    if (this.numberOfCards == 1) {
                        if (this.optional) {
                            AbstractDungeon.gridSelectScreen.open(eligibleCards, this.numberOfCards, true, text);
                        } else {
                            AbstractDungeon.gridSelectScreen.open(eligibleCards, this.numberOfCards, text, false);
                        }
                    } else if (this.optional) {
                        AbstractDungeon.gridSelectScreen.open(eligibleCards, this.numberOfCards, true, text);
                    } else {
                        AbstractDungeon.gridSelectScreen.open(eligibleCards, this.numberOfCards, text, false);
                    }

                    this.tickDuration();
                }
            } else {
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    if (this.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                        this.player.hand.addToHand(c);
                        if (this.setCost) {
                            c.setCostForTurn(this.newCost);
                        }

                        this.player.discardPile.removeCard(c);
                    }

                    c.lighten(false);
                    c.unhover();
                    c.applyPowers();
                }

                for(AbstractCard c : this.player.discardPile.group) {
                    c.unhover();
                    c.target_x = (float)CardGroup.DISCARD_PILE_X;
                    c.target_y = 0.0F;
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.tickDuration();
            if (this.isDone) {
                for (AbstractCard c : this.player.hand.group) {
                    c.applyPowers();
                }
            }

        }
    }

    private CardGroup getEligibleCards() {
        CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : this.player.discardPile.group) {
            if (this.include(c)) {
                g.addToBottom(c);
            }
        }
        return g;
    }

    private boolean include(AbstractCard c) {
        return c.type == this.cardType;
    }

    private String getText(int numberOfCards) {
        if (numberOfCards == 1) {
            if (this.cardType == AbstractCard.CardType.ATTACK) {
                return TEXT[0];
            }
            else if (this.cardType == AbstractCard.CardType.SKILL) {
                return TEXT[1];
            }
            if (this.cardType == AbstractCard.CardType.POWER) {
                return TEXT[2];
            }
        }
        else {
            if (this.cardType == AbstractCard.CardType.ATTACK) {
                return MessageFormat.format(TEXT[3], numberOfCards);
            }
            else if (this.cardType == AbstractCard.CardType.SKILL) {
                return MessageFormat.format(TEXT[4], numberOfCards);
            }
            if (this.cardType == AbstractCard.CardType.POWER) {
                return MessageFormat.format(TEXT[5], numberOfCards);
            }
        }
        throw new RuntimeException("Couldn't get text for cardType " + this.cardType.name() + " and numberOfCards " + numberOfCards);
    }

    static {
        TEXT = CardCrawlGame.languagePack.getUIString("Menagerie:CardTypeDiscardPileToHandAction").TEXT;
    }
}
