package menagerie.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class BlueExplosionSmallEffect extends AbstractGameEffect {
    private static final int EMBER_COUNT = 12;
    private final float x;
    private final float y;

    public BlueExplosionSmallEffect(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        AbstractDungeon.effectsQueue.add(new BlueDarkSmokePuffEffect(this.x, this.y));

        for(int i = 0; i < EMBER_COUNT; ++i) {
            AbstractDungeon.effectsQueue.add(new BlueSmokingEmberEffect(this.x + MathUtils.random(-50.0F, 50.0F) * Settings.scale, this.y + MathUtils.random(-50.0F, 50.0F) * Settings.scale));
        }

        CardCrawlGame.sound.playA("ORB_FROST_EVOKE", MathUtils.random(-0.2F, -0.1F));
        this.isDone = true;
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
