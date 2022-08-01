package menagerie.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.CollectorStakeEffect;

public class DarkblastEffect extends AbstractGameEffect {
    private static final int COUNT = 4;
    private final float x;
    private final float y;
    private int count;
    private float stakeTimer = 0.0F;

    public DarkblastEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.count = COUNT;
    }

    public void update() {
        this.stakeTimer -= Gdx.graphics.getDeltaTime();
        if (this.stakeTimer < 0.0F) {
            if (this.count == COUNT) {
                CardCrawlGame.sound.playA("ATTACK_HEAVY", -0.5F);
                AbstractDungeon.effectsQueue.add(new BorderLongFlashEffect(new Color(1.0F, 0.0F, 1.0F, 0.7F)));
            }

            AbstractDungeon.effectsQueue.add(new CollectorStakeEffect(this.x + MathUtils.random(-50.0F, 50.0F) * Settings.scale, this.y + MathUtils.random(-60.0F, 60.0F) * Settings.scale));
            this.stakeTimer = 0.04F;
            --this.count;
            if (this.count == 0) {
                this.isDone = true;
            }
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
