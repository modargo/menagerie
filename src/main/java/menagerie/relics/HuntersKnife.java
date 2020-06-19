package menagerie.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import menagerie.Menagerie;
import menagerie.util.TextureLoader;

public class HuntersKnife extends CustomRelic {
    public static final String ID = "Menagerie:HuntersKnife";
    private static final Texture IMG = TextureLoader.getTexture(Menagerie.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Menagerie.relicOutlineImage(ID));
    private static final int GOLD = 5;

    public HuntersKnife() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        if (m.currentHealth == 0 && !m.halfDead && !m.hasPower(MinionPower.POWER_ID)) {
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(m, this));
            this.addToBot(new GainGoldAction(GOLD));
            for (int i = 0; i < GOLD; ++i) {
                AbstractDungeon.effectList.add(new GainPennyEffect(AbstractDungeon.player, m.hb.cX, m.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, true));
            }
        }
    }

    public AbstractRelic makeCopy() {
        return new HuntersKnife();
    }
}
