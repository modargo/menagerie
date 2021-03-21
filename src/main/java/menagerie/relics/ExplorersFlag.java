package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

public class ExplorersFlag extends CustomRelic {
    public static final String ID = "Menagerie:ExplorersFlag";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static int HEAL_AMOUNT = 6;

    public ExplorersFlag() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room.getMapSymbol() != null && room.getMapSymbol().equals("?")) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(HEAL_AMOUNT);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0].replace("{0}", HEAL_AMOUNT + "");
    }

    public AbstractRelic makeCopy() {
        return new ExplorersFlag();
    }
}
