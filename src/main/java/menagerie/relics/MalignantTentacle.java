package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

import java.text.MessageFormat;

public class MalignantTentacle extends CustomRelic {
    public static final String ID = "Menagerie:MalignantTentacle";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int CONSTRICTED = 1;
    private static final int TURNS = 2;

    public MalignantTentacle() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], TURNS, CONSTRICTED);
    }

    @Override
    public void atBattleStart() {
        this.counter = TURNS - 1;
    }

    @Override
    public void atTurnStart() {
        this.counter = (this.counter + 1) % TURNS;
        if (this.counter == 0) {
            this.flash();
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ConstrictedPower(AbstractDungeon.player, AbstractDungeon.player, CONSTRICTED), CONSTRICTED));
        }
    }

    public AbstractRelic makeCopy() {
        return new MalignantTentacle();
    }
}
