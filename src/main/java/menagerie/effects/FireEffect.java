package menagerie.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeDur;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeIntensity;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.RedFireballEffect;

public class FireEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private int intensity;

    public FireEffect(float x, float y, int intensity) {
        this.x = x;
        this.y = y;
        this.intensity = intensity;
        CardCrawlGame.screenShake.shake(ShakeIntensity.HIGH, ShakeDur.SHORT, true);
    }

    public void update() {
        CardCrawlGame.sound.playA("ATTACK_FIRE", 0.3F);
        float dst = 180.0F + (float)this.intensity * 3.0F;
        AbstractDungeon.effectsQueue.add(new RedFireballEffect(this.x - dst * Settings.scale, this.y, this.x + dst * Settings.scale, this.y - 50.0F * Settings.scale, this.intensity));
        this.isDone = true;
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
